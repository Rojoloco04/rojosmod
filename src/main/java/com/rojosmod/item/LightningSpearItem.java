package com.rojosmod.item;

import com.rojosmod.ModEntities;
import com.rojosmod.entity.LightningSpearEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LightningSpearItem extends Item {

    // In 1.21.x, Item.Properties must have the ResourceKey set before the item is constructed.
    // We receive the fully configured Properties from ModItems so we don't have to build them here.
    public LightningSpearItem(Properties properties) {
        super(properties);
    }

    // use() is called when the player right-clicks while holding this item.
    // It runs on both the client and server — the !level.isClientSide() guard
    // ensures we only spawn the entity on the server, which then syncs to clients.
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // Create the spear entity, positioned at the player and aimed in their look direction.
            // We pass `stack` so the entity knows which item it represents visually.
            LightningSpearEntity spear = new LightningSpearEntity(
                    ModEntities.LIGHTNING_SPEAR.get(), player, level, stack);

            // shootFromRotation points the projectile based on where the player is looking.
            // Parameters: shooter, xRot (pitch), yRot (yaw), tilt, velocity, inaccuracy
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);

            level.addFreshEntity(spear);

            // Consume one spear from the stack unless the player is in creative mode
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        // Add a 1-second cooldown (20 ticks) so it can't be spammed.
        // In 1.21.x addCooldown takes an ItemStack, not the Item itself.
        player.getCooldowns().addCooldown(stack, 20);

        // SUCCESS on client triggers the arm-swing animation.
        // CONSUME on server tells Minecraft the action was handled.
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
}
