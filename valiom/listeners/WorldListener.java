package com.valiom.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true); // Empêcher la pluie
        }
    }

    public static void applyFreezeOnAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false"); // Stopper le cycle jour/nuit
            world.setTime(6000); // Fixer à midi
            world.setStorm(false); // Pas de pluie
            world.setThundering(false); // Pas d'orage
        }
    }
}
