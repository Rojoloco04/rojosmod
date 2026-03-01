# Claude Context — Rojo's Mod

## Project Summary

This is a Minecraft Forge mod for **Minecraft 1.21.11** using **Forge 61.0.8** and **Java 21**. The developer (Rojoloco) is learning Java through modding. Keep explanations clear and educational — don't just fix things, explain why.

## Key Facts

- **Mod ID**: `rojosmod`
- **Main class**: `src/main/java/com/rojosmod/RojosMod.java`
- **Config class**: `src/main/java/com/rojosmod/Config.java`
- **Mod metadata**: `src/main/resources/META-INF/mods.toml`
- **Build file**: `build.gradle`
- **Properties**: `gradle.properties`
- **IDE**: Eclipse (launch configs present), but Gradle CLI works fine
- **Java version**: 21
- **License**: MIT
- **Author**: Rojoloco

## Running

```bash
./gradlew runClient    # Launch Minecraft with mod
./gradlew runServer    # Launch dedicated server
./gradlew runData      # Run data generators
./gradlew build        # Build the mod JAR (output in build/libs/)
```

## Architecture: How Forge Mods Work

### Entry Point
`@Mod("rojosmod")` on `RojosMod.java` marks it as the mod entry point. The constructor runs at load time.

### Event Buses
Forge has two event buses:
- **`MOD` bus** (`modEventBus`) — lifecycle events (setup, registration, client setup)
- **`FORGE` bus** (`MinecraftForge.EVENT_BUS`) — gameplay events (player join, block break, etc.)

Register listeners in the constructor via:
```java
modEventBus.addListener(this::commonSetup);
```
Or use `@Mod.EventBusSubscriber` + `@SubscribeEvent` for static methods.

### Registration Pattern
Always use `DeferredRegister` — never register things directly:
```java
public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

public static final RegistryObject<Item> MY_ITEM =
    ITEMS.register("my_item", () -> new Item(new Item.Properties()));
```
Then in the constructor: `ITEMS.register(modEventBus);`

### Config System
`Config.java` uses `ForgeConfigSpec`. The `ENABLED` boolean is a basic example. Add new options in the `BUILDER` block.

## Coding Conventions for This Project

- Package: `com.rojosmod`
- Use `LogUtils.getLogger()` for logging (already imported in RojosMod.java)
- Follow Forge naming conventions: snake_case for registry names, PascalCase for Java classes
- Keep code simple and readable — this is a learning project

## What Doesn't Exist Yet

- No custom items, blocks, or entities
- No textures or models
- No recipes or loot tables
- `src/generated/` is empty (data gen hasn't been run)

## Common Gotchas

- Registration must happen during the correct phase — don't register items during gameplay events
- `RegistryObject` values are not available until after registration phase; don't call `.get()` too early
- Client-only code must be inside `@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = ...)`
- `run/` and `run-data/` directories are gitignored — they're generated at runtime
