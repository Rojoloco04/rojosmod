# Rojo's Mod

A Minecraft Forge mod for Minecraft 1.21.11, built as a Java learning project.

## What This Is

This is a Minecraft mod skeleton built on top of the **NeoForge/Forge MDK** (Mod Development Kit). It's a starting point for adding custom items, blocks, entities, and game mechanics to Minecraft using Java.

**Stack:**
- Minecraft 1.21.11
- Forge 61.0.8
- Java 21
- Gradle 8.x build system
- Eclipse IDE (configured, but IntelliJ also works)

## Project Structure

```
src/
  main/
    java/com/rojosmod/
      RojosMod.java     - Main mod entry point (@Mod annotation, event registration)
      Config.java       - Forge config system (reads/writes config files in run/config/)
    resources/
      META-INF/
        mods.toml       - Mod metadata (name, version, dependencies)
      pack.mcmeta       - Resource pack metadata
```

## How Forge Mods Work (Quick Overview)

Minecraft Forge uses an **event bus** system. Your mod registers listeners for game events and reacts to them:

1. **`@Mod("rojosmod")`** — tells Forge this class is the mod's entry point
2. **`FMLCommonSetupEvent`** — fires during startup on both client and server; good for registering things
3. **`FMLClientSetupEvent`** — fires only on the client; good for rendering setup
4. **`@Mod.EventBusSubscriber`** — auto-registers static `@SubscribeEvent` methods on the event bus

## Running the Mod

From Eclipse, use the provided launch configurations:
- **runClient** — launches Minecraft with your mod loaded
- **runServer** — launches a dedicated server
- **runData** — runs data generators (auto-creates JSON files for blocks/items)

From the command line:
```bash
./gradlew runClient
./gradlew runServer
```

## Adding Things

Common next steps when learning:
- **New item** — register a `DeferredRegister<Item>` and add an `Item` instance
- **New block** — same pattern with `DeferredRegister<Block>`
- **Event listener** — add a `@SubscribeEvent` method or register via `modEventBus.addListener()`
- **Config option** — add a field to `Config.java` using `ForgeConfigSpec.Builder`

## Key Resources

- [Forge Documentation](https://docs.minecraftforge.net/)
- [Forge Community Wiki](https://forge.gemwire.uk/wiki/Main_Page)
- [Mod ID]: `rojosmod`
