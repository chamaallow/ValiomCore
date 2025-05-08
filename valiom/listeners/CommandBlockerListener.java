package com.valiom.listeners;

import com.valiom.ValiomCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class CommandBlockerListener implements Listener {

    private final List<String> allowedCommands = Arrays.asList(
            "/spawn",
            "/duel",
            "/ranked",
            "/kit"
    );

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();

        // Si joueur non OP
        if (!player.isOp()) {
            // Autoriser que certaines commandes
            if (allowedCommands.stream().noneMatch(message::startsWith)) {
                event.setCancelled(true);
                player.sendMessage("§cCommande inconnue.");
            }
        }
    }
}
