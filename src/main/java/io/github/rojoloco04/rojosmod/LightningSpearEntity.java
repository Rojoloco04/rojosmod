package io.github.rojoloco04.rojosmod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class LightningSpearEntity extends AbstractArrow {

    // Used by EntityType factory
    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, Level level) {
        super(type, level);
    }

    // Used by Projectile.spawnProjectileFromRotation (ProjectileFactory interface)
    public LightningSpearEntity(Level level, LivingEntity owner, ItemStack pickupStack) {
        super(rojosmod.LIGHTNING_SPEAR_ENTITY.get(), owner, level, pickupStack, null);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        DamageSource source = this.damageSources().trident(this, owner == null ? this : owner);
        target.hurt(source, 8.0f);

        if (level() instanceof ServerLevel serverLevel) {
            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, serverLevel);
            lightning.setPos(target.getX(), target.getY(), target.getZ());
            serverLevel.addFreshEntity(lightning);
        }
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.discard();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(rojosmod.LIGHTNING_SPEAR.get());
    }
}
