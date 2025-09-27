package com.violetsmc.skeletonBoss;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.violetsmc.skeletonBoss.SkeletonSpawnListener;

public class SkeletonBoss extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the skeleton spawn listener
        Bukkit.getPluginManager().registerEvents(new SkeletonSpawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomBowListener(this), this);
        getLogger().info("SkeletonBoss Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SkeletonBoss Plugin has been disabled!");
    }
}
