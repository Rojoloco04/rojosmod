# rojosmod

A NeoForge mod for Minecraft 1.21.11 adding the **Lightning Spear** — a throwable copper weapon that strikes lightning on hit.

## Requirements

- Minecraft 1.21.11
- NeoForge 21.11.38-beta (or compatible)

## Items

### Lightning Spear

*"Strike down your foes..."*

A throwable copper spear. Hold right-click to charge, release to throw.

- **Melee damage:** 11 (1.0 attack speed, stab animation)
- **Thrown damage:** 11
- **Durability:** 200 (repaired with copper ingots)
- **Enchantability:** 20
- **On hit (thrown):** Summons a lightning bolt at the target, loses 1 durability, falls to the ground
- **Loyalty:** Supported — returns to thrower like a trident
- **Unbreaking / Mending:** Supported
- **Sounds:** Trident throw / hit / hit-ground

**Crafting:**

<img width="602" height="286" alt="image" src="https://github.com/user-attachments/assets/cdae8184-7e5e-4eef-bd35-d98c1762e671" />


## Development

Built with [NeoForge MDK](https://github.com/neoforged/MDK) using [Parchment](https://parchmentmc.org/) mappings (2025.12.20 for 1.21.11).

### Project structure

```
src/main/java/io/github/rojoloco04/rojosmod/
  rojosmod.java               — registration (items, entities, creative tab)
  rojosmodClient.java         — client setup, renderer + model layer registration
  LightningSpearItem.java     — item behaviour (charging, throwing, attributes, lore)
  LightningSpearEntity.java   — projectile entity (damage, lightning, loyalty, pickup)
  LightningSpearModel.java    — 3D entity model (Blockbench export, 32x32 UV)
  LightningSpearRenderer.java — entity renderer

src/main/resources/
  assets/rojosmod/
    textures/item/lightning_spear.png    — inventory/in-hand sprite
    textures/entity/lightning_spear.png  — in-flight 3D model texture
    models/item/lightning_spear.json     — item model (handheld)
    lang/en_us.json                      — display names
  data/
    rojosmod/recipe/lightning_spear.json — crafting recipe
    minecraft/tags/item/enchantable/
      trident.json                       — loyalty/channeling/impaling support
      durability.json                    — unbreaking/mending support

models/lightning_spear.bbmodel  — Blockbench source for the entity model
```

### Running

```
./gradlew runClient   # launch game client
./gradlew runServer   # launch dedicated server
./gradlew build       # build mod jar
./gradlew clean       # clear build cache
```
