package com.violetsmc.skeletonBoss;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Random;

public class CustomBowListener implements Listener {

    private final Plugin plugin;

    public CustomBowListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent event) {
        // Check if the entity shooting is a Player
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();  // Cast entity to Player

            // Check if the player is shooting a bow with custom powers
            ItemStack bow = event.getBow();
            if (bow != null && bow.getItemMeta() != null) {
                String bowName = bow.getItemMeta().getDisplayName();

                // Check if it's the custom TNT Bow
                if ("TNT Bow".equals(bowName)) {
                    event.setCancelled(true);  // Cancel the normal arrow shot
                    launchTNT(player);  // Launch TNT instead
                }

                // Check if it's the custom Wither Skull Bow
                if ("Wither Skull Bow".equals(bowName)) {
                    event.setCancelled(true);  // Cancel the normal arrow shot
                    launchWitherSkull(player);  // Launch Wither Skull instead
                }
            }
        }
    }

    private void launchTNT(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = player.getLocation().getDirection();

        // Spawn lit TNT and set its direction
        TNTPrimed tnt = player.getWorld().spawn(playerLocation.add(0, 1, 0), TNTPrimed.class);
        tnt.setVelocity(direction.multiply(1.5));  // Adjust speed as needed
        tnt.setFuseTicks(40);  // Set fuse time (2 seconds before explosion)
        tnt.setYield(3);  // Explosion radius
    }

    private void launchWitherSkull(Player player) {
        Location playerLocation = player.getLocation();
        Vector direction = player.getLocation().getDirection();

        // Spawn wither skull and set its direction
        WitherSkull skull = player.getWorld().spawn(playerLocation.add(0, 1, 0), WitherSkull.class);
        skull.setVelocity(direction.multiply(2));  // Speed up the wither skull
        skull.setCharged(true);  // Make it a charged wither skull
        skull.setYield(6);  // Explosion radius for wither skull
    }

    @EventHandler
    public void onBossSkeletonDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) event.getEntity();
            Random random = new Random();

            // Check if the skeleton is a Skeletron or Wither Skeletron and give the bow with a chance
            if (skeleton.getCustomName() != null) {
                String name = skeleton.getCustomName();
                ItemStack bow;

                // Determine which bow to drop based on the name
                if ("Skeletron".equals(name) && random.nextInt(100) < 20) { // 20% chance for TNT Bow
                    bow = createCustomBow("TNT Bow", "Shoots Explosive TNT");
                    skeleton.getWorld().dropItem(skeleton.getLocation(), bow);
                } else if ("Wither Skeletron".equals(name) && random.nextInt(100) < 20) { // 20% chance for Wither Skull Bow
                    bow = createCustomBow("Wither Skull Bow", "Shoots Wither Skulls");
                    skeleton.getWorld().dropItem(skeleton.getLocation(), bow);
                }
            }
        }
    }

    private ItemStack createCustomBow(String bowName, String lore) {
        // Create the custom bow
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName(bowName);
        meta.setLore(List.of(lore)); // Set lore
        bow.setItemMeta(meta);
        bow.addEnchantment(Enchantment.INFINITY, 1);
        bow.addEnchantment(Enchantment.UNBREAKING, 3);  // Unbreaking III

        return bow;
    }
}
