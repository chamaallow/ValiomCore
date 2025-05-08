package com.valiom.listeners;

import com.valiom.ValiomCore;
import com.valiom.api.managers.ProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ProfileManager profileManager = ValiomCore.getAPI().getProfileManager();
        profileManager.saveProfile(event.getPlayer().getUniqueId());
        profileManager.deleteProfile(event.getPlayer().getUniqueId());
    }
}
