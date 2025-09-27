package com.violetsmc.skeletonBoss;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SkeletonSpawnListener implements Listener {

    private final Plugin plugin;
    private final Random random = new Random();
    private final Map<Skeleton, BossBar> bossBarMap = new HashMap<>();  // Track each boss skeleton and its boss bar

    public SkeletonSpawnListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSkeletonSpawn(CreatureSpawnEvent event) {
        // Check if the entity is a skeleton
        if (event.getEntity().getType() == EntityType.SKELETON) {
            int chance = random.nextInt(100);

            if (chance < 10) {
                // 10% chance for the TNT boss
                Skeleton skeleton = (Skeleton) event.getEntity();
                spawnTNTBoss(skeleton);
            } else if (chance < 15) {
                // 5% chance for the wither boss
                Skeleton skeleton = (Skeleton) event.getEntity();
                spawnWitherBoss(skeleton);
            }
        }
    }

    private void spawnTNTBoss(Skeleton skeleton) {
        // TNT Boss: Same as before
        equipBossSkeleton(skeleton, "Skeletron", Material.NETHERITE_HELMET);  // Regular netherite armor
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);  // 2x health
        skeleton.setHealth(40.0);

        // Create a boss bar for the TNT boss
        BossBar bossBar = Bukkit.createBossBar("Skeletron", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBarMap.put(skeleton, bossBar);
    }

    private void spawnWitherBoss(Skeleton skeleton) {
        // Wither Boss: Shoots wither skulls and wears vex armor
        equipBossSkeleton(skeleton, "Wither Skeletron", Material.DIAMOND_HELMET);  // Armor representing Vex trim
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);  // 2x health
        skeleton.setHealth(40.0);

        // Create a boss bar for the wither boss
        BossBar bossBar = Bukkit.createBossBar("Wither Skeletron", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setProgress(1.0);
        bossBarMap.put(skeleton, bossBar);
    }

    private void equipBossSkeleton(Skeleton skeleton, String name, Material helmetType) {
        // Equip the skeleton with armor and set the name
        ItemStack helmet = new ItemStack(helmetType);
        helmet.addEnchantment(Enchantment.PROTECTION, 4);
        helmet.addEnchantment(Enchantment.MENDING, 1);
        helmet.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        chestplate.addEnchantment(Enchantment.PROTECTION, 4);
        chestplate.addEnchantment(Enchantment.MENDING, 1);
        chestplate.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack leggings = new ItemStack(Material.NETHERITE_LEGGINGS);
        leggings.addEnchantment(Enchantment.PROTECTION, 4);
        leggings.addEnchantment(Enchantment.MENDING, 1);
        leggings.addEnchantment(Enchantment.UNBREAKING, 3);

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION, 4);
        boots.addEnchantment(Enchantment.FEATHER_FALLING, 4);
        boots.addEnchantment(Enchantment.MENDING, 1);
        boots.addEnchantment(Enchantment.UNBREAKING, 3);

        // Set armor
        skeleton.getEquipment().setHelmet(helmet);
        skeleton.getEquipment().setChestplate(chestplate);
        skeleton.getEquipment().setLeggings(leggings);
        skeleton.getEquipment().setBoots(boots);

        // Set custom name
        skeleton.setCustomName(name);
        skeleton.setCustomNameVisible(true);

        // Set armor to not drop on death
        skeleton.getEquipment().setHelmetDropChance(0.0f);
        skeleton.getEquipment().setChestplateDropChance(0.0f);
        skeleton.getEquipment().setLeggingsDropChance(0.0f);
        skeleton.getEquipment().setBootsDropChance(0.0f);
    }

    @EventHandler
    public void onSkeletonShootBow(EntityShootBowEvent event) {
        // Check if the entity shooting is a skeleton and if it's a boss skeleton
        if (event.getEntity() instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) event.getEntity();

            if (isTNTBoss(skeleton)) {
                // Cancel the normal arrow shot for the TNT boss
                event.setCancelled(true);
                launchTNT(skeleton);
            } else if (isWitherBoss(skeleton)) {
                // Cancel the normal arrow shot for the Wither boss
                event.setCancelled(true);
                launchWitherSkull(skeleton);
            }
        }
    }

    private void launchTNT(Skeleton skeleton) {
        Location skeletonLocation = skeleton.getLocation();
        Vector direction = skeleton.getLocation().getDirection();

        // Spawn lit TNT, ensuring it won't damage the skeleton itself
        TNTPrimed tnt = skeleton.getWorld().spawn(skeletonLocation.add(0, 1, 0), TNTPrimed.class);
        tnt.setVelocity(direction.multiply(1.5));  // Adjust speed as needed
        tnt.setFuseTicks(40);  // Set fuse time (2 seconds before explosion)
        tnt.setIsIncendiary(false);  // Disable fire
        tnt.setYield(6);  // Explosion radius doubled for 2x damage (default is 3)
    }

    private void launchWitherSkull(Skeleton skeleton) {
        // Find the nearest player to the skeleton to target (similar to how the Wither Boss targets)
        Player target = findNearestPlayer(skeleton);

        if (target != null) {
            // Get the direction towards the target
            Location skeletonLocation = skeleton.getLocation();
            Location targetLocation = target.getLocation();
            Vector direction = targetLocation.toVector().subtract(skeletonLocation.toVector()).normalize();

            // Spawn wither skull and shoot toward the target
            WitherSkull skull = skeleton.getWorld().spawn(skeletonLocation.add(0, 1, 0), WitherSkull.class);
            skull.setCharged(true);  // Make it a charged wither skull

            // Set the velocity directly to make the skull fly faster
            Vector velocity = direction.multiply(3);  // Increase speed by adjusting this value (2x is faster)
            skull.setVelocity(velocity);  // Apply the velocity to the wither skull

            // Set explosion yield to increase damage
            skull.setYield(10);  // 3x damage effect

            // Custom logic to apply extra damage if needed
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // On explosion, apply extra damage to nearby entities
                skull.getNearbyEntities(5, 5, 5).forEach(entity -> {
                    if (entity instanceof LivingEntity && !(entity.equals(skeleton))) {  // Ensure the entity can take damage and is not the Wither Skeletron itself
                        ((LivingEntity) entity).damage(15.0);  // 3x damage value (15.0 for stronger effect)
                    }
                });
            }, 20L);  // Delay this task for 1 second
        }
    }



    private Player findNearestPlayer(Skeleton skeleton) {
        Player nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : skeleton.getWorld().getPlayers()) {
            double distance = skeleton.getLocation().distance(player.getLocation());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = player;
            }
        }

        return nearestPlayer;
    }

    private boolean isTNTBoss(Skeleton skeleton) {
        return "Skeletron".equals(skeleton.getCustomName());
    }

    private boolean isWitherBoss(Skeleton skeleton) {
        return "Wither Skeletron".equals(skeleton.getCustomName());
    }

    // Handle player movement and boss bar display (same as before)
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        for (Skeleton skeleton : player.getWorld().getEntitiesByClass(Skeleton.class)) {
            if (isTNTBoss(skeleton) || isWitherBoss(skeleton)) {
                double distance = playerLocation.distance(skeleton.getLocation());

                // If the player is within 30 blocks, display the boss bar
                BossBar bossBar = bossBarMap.get(skeleton);
                if (distance <= 30 && bossBar != null) {
                    bossBar.addPlayer(player);
                    double progress = skeleton.getHealth() / skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    bossBar.setProgress(progress);
                } else if (bossBar != null) {
                    bossBar.removePlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onSkeletonDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) event.getEntity();

            if (isTNTBoss(skeleton) || isWitherBoss(skeleton)) {
                BossBar bossBar = bossBarMap.remove(skeleton);
                if (bossBar != null) {
                    bossBar.removeAll();
                }
            }
        }
    }
}
