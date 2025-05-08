package com.valiom.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabBlockerListener {

    private final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public void register() {
        protocolManager.addPacketListener(new PacketAdapter(com.valiom.ValiomCore.getInstance(), PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                // Si le joueur est OP ➔ il voit tout
                if (player.isOp()) {
                    return;
                }

                // On récupère ce que tape le joueur
                String message = event.getPacket().getStrings().readSafely(0);
                if (message == null) return; // Si vide, ignorer

                message = message.toLowerCase();

                // 👉 Important : ne filtrer QUE si ça commence par un "/"
                if (message.startsWith("/")) {

                    // Récupérer la commande sans slash
                    String command = message.split(" ")[0].replaceFirst("/", "");

                    // Si c’est une commande plugin ➔ OK
                    if (Bukkit.getPluginCommand(command) != null) {
                        return;
                    }

                    // Sinon ➔ BLOQUER
                    event.setCancelled(true);
                }
            }
        });
    }
}
