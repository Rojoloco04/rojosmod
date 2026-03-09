package io.github.rojoloco04.rojosmod;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * The thrown lightning spear projectile.
 * Extends AbstractArrow for physics, pickup, and in-ground sticking.
 * On entity hit: deals 11 damage, summons lightning, loses 1 durability, then falls to the ground.
 * Supports Loyalty enchantment — returns to thrower like a trident.
 */
public class LightningSpearEntity extends AbstractArrow {

    // Synced to client so loyalty return animation plays correctly on both sides
    private static final EntityDataAccessor<Byte> ID_LOYALTY =
        SynchedEntityData.defineId(LightningSpearEntity.class, EntityDataSerializers.BYTE);

    // True once the spear has hit something — triggers loyalty return, prevents re-hitting
    private boolean dealtDamage = false;

    // Incremented each tick while returning; used to play the return sound only once
    public int clientSideReturnTickCount;

    // Used by EntityType factory (deserialization / /summon command)
    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, Level level) {
        super(type, level);
    }

    // Used by Projectile.spawnProjectileFromRotation when the player throws
    public LightningSpearEntity(Level level, LivingEntity owner, ItemStack pickupStack) {
        super(rojosmod.LIGHTNING_SPEAR_ENTITY.get(), owner, level, pickupStack, null);
        this.entityData.set(ID_LOYALTY, getLoyaltyLevel(pickupStack));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ID_LOYALTY, (byte) 0);
    }

    @Override
    public void tick() {
        // After sitting in the ground for 4 ticks, treat it as having dealt damage
        // so loyalty return kicks in for block hits too (not just entity hits)
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity owner = this.getOwner();
        int loyalty = this.entityData.get(ID_LOYALTY);

        if (loyalty > 0 && (this.dealtDamage || this.isNoPhysics()) && owner != null) {
            if (!isValidReturnOwner()) {
                // Owner is dead/spectating — drop the spear as an item instead of returning
                if (this.level() instanceof ServerLevel serverLevel && this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(serverLevel, this.getPickupItem(), 0.1F);
                }
                this.discard();
            } else {
                // Fly toward the owner's eye position, accelerating based on loyalty level
                this.setNoPhysics(true);
                Vec3 toOwner = owner.getEyePosition().subtract(this.position());
                this.setPosRaw(this.getX(), this.getY() + toOwner.y * 0.015 * loyalty, this.getZ());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(toOwner.normalize().scale(0.05 * loyalty)));
                if (this.clientSideReturnTickCount == 0) {
                    this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
                }
                this.clientSideReturnTickCount++;
            }
        }

        super.tick();
    }

    // Owner must be alive and not in spectator mode for the spear to return
    private boolean isValidReturnOwner() {
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) return false;
        if (owner instanceof net.minecraft.server.level.ServerPlayer sp) return !sp.isSpectator();
        return true;
    }

    // Once dealtDamage is true, skip hit detection so the returning spear can't hurt things
    @Override
    protected @Nullable EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
        return this.dealtDamage ? null : super.findHitEntity(from, to);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().trident(this, owner == null ? this : owner);

        this.dealtDamage = true;

        if (level() instanceof ServerLevel serverLevel) {
            target.hurtServer(serverLevel, source, 11.0f);

            // Summon lightning at the target's feet
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
            lightning.setPos(target.getX(), target.getY(), target.getZ());
            serverLevel.addFreshEntity(lightning);

            // Only consume durability in survival — creative players have infinite materials
            if (!(owner instanceof Player p && p.hasInfiniteMaterials())) {
                this.getPickupItemStackOrigin().hurtAndBreak(1, serverLevel, null, item -> {});
            }
        }

        this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
        // Stop forward movement so the spear falls straight down to the ground
        this.setDeltaMovement(0, 0, 0);
    }

    // Only the owner (or anyone, if ownerless) can trigger pickup — prevents others from stealing mid-return
    @Override
    public void playerTouch(Player player) {
        if (this.ownedBy(player) || this.getOwner() == null) {
            super.playerTouch(player);
        }
    }

    @Override
    protected boolean tryPickup(Player player) {
        // Creative players: discard the entity without adding to inventory
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        // Also allow pickup while mid-return (isNoPhysics = true during loyalty flight)
        return super.tryPickup(player)
            || (this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem()));
    }

    // Don't despawn while the player has loyalty — they should always get it back
    @Override
    public void tickDespawn() {
        if (this.pickup != AbstractArrow.Pickup.ALLOWED || this.entityData.get(ID_LOYALTY) <= 0) {
            super.tickDespawn();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.dealtDamage = input.getBooleanOr("DealtDamage", false);
        // Re-read loyalty from the pickup item in case enchantments changed while saved
        this.entityData.set(ID_LOYALTY, getLoyaltyLevel(this.getPickupItemStackOrigin()));
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(rojosmod.LIGHTNING_SPEAR.get());
    }

    // Loyalty level is read from the enchantment on the item; only available server-side
    private byte getLoyaltyLevel(ItemStack stack) {
        return level() instanceof ServerLevel serverLevel
            ? (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(serverLevel, stack, this), 0, 127)
            : 0;
    }
}
