package com.rojosmod;

import com.rojosmod.item.LightningSpearItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    // DeferredRegister holds all item registrations for this mod.
    // Think of it as a list that Forge reads at startup to add our stuff to the game.
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RojosMod.MODID);

    // RegistryObject is a lazy reference — the actual item isn't created until Forge
    // runs the registration phase. Don't call .get() before then.
    public static final RegistryObject<Item> LIGHTNING_SPEAR =
            ITEMS.register("lightning_spear", () ->
                    // In 1.21.x every item's Properties must have its ResourceKey set via setId().
                    // ITEMS.key("lightning_spear") gives us the correct ResourceKey<Item>
                    // without needing to call the registry — it's safe to call here.
                    new LightningSpearItem(new Item.Properties()
                            .stacksTo(16)
                            .setId(ITEMS.key("lightning_spear"))));

    // This inner class adds our items to creative mode tabs.
    // In Forge 61.x, BuildCreativeModeTabContentsEvent is on the default (Forge) bus,
    // so no bus parameter is needed here.
    @Mod.EventBusSubscriber(modid = RojosMod.MODID)
    public static class CreativeTabEvents {

        @SubscribeEvent
        public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
            // Add the lightning spear to the Combat tab so it's easy to find in creative mode
            if (event.getTabKey() == CreativeModeTabs.COMBAT) {
                event.accept(LIGHTNING_SPEAR.get());
            }
        }
    }
}
