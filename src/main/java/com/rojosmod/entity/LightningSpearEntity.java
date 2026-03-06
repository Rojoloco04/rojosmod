package com.rojosmod.entity;

import com.rojosmod.ModItems;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class LightningSpearEntity extends ThrowableItemProjectile {

    // This constructor is used when spawning the entity from nothing (e.g. loading from save).
    // Forge requires it for entity registration.
    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, Level level) {
        super(type, level);
    }

    // This constructor is used when a player throws the spear.
    // Passing the thrower sets the owner, so the spear doesn't collide with the player who threw it.
    // In 1.21.x the ItemStack must also be passed so the entity knows what item to display.
    public LightningSpearEntity(EntityType<? extends LightningSpearEntity> type, LivingEntity thrower, Level level, ItemStack stack) {
        super(type, thrower, level, stack);
    }

    // Tells Minecraft which item to display as the spear's visuals in the world.
    @Override
    protected Item getDefaultItem() {
        return ModItems.LIGHTNING_SPEAR.get();
    }

    // Called when the spear hits an entity (mob, player, etc.)
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result); // let Forge/Minecraft handle ownership tracking

        if (!level().isClientSide()) {
            // Summon a lightning bolt at the target's position
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level(), EntitySpawnReason.TRIGGERED);
            if (lightning != null) {
                lightning.setPos(result.getEntity().getX(),
                                 result.getEntity().getY(),
                                 result.getEntity().getZ());
                level().addFreshEntity(lightning);
            }
        }

        // Remove the spear from the world after it hits something
        this.discard();
    }

    // Called when the spear hits a block — just remove it, no lightning
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.discard();
    }
}
