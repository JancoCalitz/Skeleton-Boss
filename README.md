# SkeletonBoss

SkeletonBoss is a custom Minecraft plugin for Paper/Spigot servers.  
It enhances regular skeleton spawns by occasionally upgrading them into powerful boss variants with unique abilities, boss bars, and special loot drops.

## Overview
When a skeleton spawns, there is a small chance it will transform into one of two bosses:  
- **Skeletron (TNT Boss):** Equipped with enchanted netherite armour, launches TNT instead of arrows.  
- **Wither Skeletron (Wither Boss):** Wears diamond/vex-style armour, shoots charged wither skulls, and applies high damage to nearby players.  

Each boss has a visible **BossBar** to track health and creates a more challenging and exciting fight experience.  
In addition, defeating a boss grants players a chance to receive a rare **custom bow** with explosive or wither powers.

## Features
- Chance-based spawning of enhanced skeleton bosses.  
- Two unique boss types: TNT-launching and Wither Skull-launching.  
- Custom **BossBar** display visible to nearby players.  
- Stronger stats (2x health, enchanted armour).  
- Special loot drops:  
  - **TNT Bow** (shoots TNT).  
  - **Wither Skull Bow** (shoots Wither Skulls).  
- Balanced drop rates (20% chance from each boss).  

## Technical
- **Minecraft:** Spigot/Paper 1.21.1  
- **Language:** Java 21  
- **Build Tool:** Maven  

## Installation
1. Build the plugin with Maven (`mvn clean package`) or use a precompiled JAR.  
2. Place the JAR into your server’s `plugins/` directory.  
3. Start the server — no configuration is required.  
4. Skeletons will now occasionally spawn as boss variants.  

---

## License & Usage
This plugin was developed by **Penta** and is shown here for demonstration purposes. Rights reserved.
