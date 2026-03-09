package io.github.rojoloco04.rojosmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class LightningSpearItem extends Item {

    public LightningSpearItem(Properties properties) {
        super(properties);
    }

    /**
     * Sets all item properties: durability, enchantability, repair material,
     * attack attributes, swing animation, and lore text.
     * Called during registration so the Properties object carries the right resource key.
     */
    static Item.Properties buildProperties(Item.Properties p) {
        return p.durability(200)
                .enchantable(20)
                .repairable(Tags.Items.INGOTS_COPPER)
                .attributes(ItemAttributeModifiers.builder()
                        // 1.0 base + 10.0 bonus = 11 melee damage
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 10.0, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        // 4.0 base + (-3.0) bonus = 1.0 attack speed
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3.0, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build())
                // STAB animation, 20 ticks = 1 second (matches attack speed)
                .component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, 20))
                .component(DataComponents.LORE, new ItemLore(List.of(
                        Component.literal("Strike down your foes...")
                )));
    }

    // Prevent block breaking in creative mode (swords and axes allow it by default)
    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
        return !(entity instanceof Player player && player.isCreative());
    }

    // Show the trident hold-and-throw animation while charging
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT;
    }

    // Effectively infinite use duration — the player releases manually
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    // Right-click begins charging
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    // Release right-click: throw the spear if charged for at least 10 ticks
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return false;

        int charge = getUseDuration(stack, entity) - timeLeft;
        if (charge < 10) return false;

        if (level instanceof ServerLevel serverLevel) {
            // Speed scales from 0 to 2.5 blocks/tick over the first second of charge
            float speed = Math.min(charge / 20.0f, 1.0f) * 2.5f;
            player.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F);
            // consumeAndReturn removes 1 from the stack and returns the consumed ItemStack,
            // which becomes the projectile's pickup item (preserving enchantments, damage, etc.)
            ItemStack thrown = stack.consumeAndReturn(1, player);
            Projectile.spawnProjectileFromRotation(
                LightningSpearEntity::new, serverLevel, thrown, player, 0.0f, speed, 1.0f
            );
        }
        return true;
    }
}
