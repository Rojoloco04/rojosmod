package io.github.rojoloco04.rojosmod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public class LightningSpearItem extends Item {

    public LightningSpearItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;

        int charge = getUseDuration(stack, entity) - timeLeft;
        if (charge < 10) return false;

        if (level instanceof ServerLevel serverLevel) {
            // Scale throw speed with charge — reaches max (2.5f) after ~1 second
            float speed = Math.min(charge / 20.0f, 1.0f) * 2.5f;
            ItemStack thrown = stack.consumeAndReturn(1, player);
            Projectile.spawnProjectileFromRotation(
                LightningSpearEntity::new, serverLevel, thrown, player, 0.0f, speed, 1.0f
            );
        }
        return true;
    }
}
