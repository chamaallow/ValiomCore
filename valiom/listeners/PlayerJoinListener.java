package com.valiom.listeners;

import com.valiom.ValiomCore;
import com.valiom.api.managers.ProfileManager;
import com.valiom.core.managers.ProfileManagerImpl;
import com.valiom.models.Profile;
import com.valiom.utils.LuckPermUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProfileManagerImpl profileManager = (ProfileManagerImpl) ValiomCore.getAPI().getProfileManager();

        profileManager.loadProfileAsync(player.getUniqueId(), profile -> {
            if (profile == null) {
                player.kickPlayer("§cUne erreur est survenue lors du chargement de votre profil. Veuillez réessayer.");
                return;
            }

            // ✅ Profil chargé avec succès
            Bukkit.getLogger().info("✅ Profil chargé pour " + player.getName());

            LuckPermUtils.assignDefaultGroupIfNeeded(player);


            // 💡 Ici tu peux setup l'inventaire, scoreboard, etc.
            // Par exemple : ValiomPractice.getInstance().getScoreboardManager().setContext(player, ScoreboardContext.LOBBY);
        });
    }
}
