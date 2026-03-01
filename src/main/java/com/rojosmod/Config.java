package com.rojosmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// Minimal Forge config holder
@Mod.EventBusSubscriber(modid = RojosMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Example toggle you can actually use later (can delete if you want ZERO options)
    public static final ForgeConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Whether the mod is enabled")
            .define("enabled", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // Cached values
    public static boolean enabled;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enabled = ENABLED.get();
    }
}