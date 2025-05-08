package com.valiom;

import com.valiom.api.ValiomAPI;
import com.valiom.core.ValiomAPIImpl;
import com.valiom.core.managers.ProfileManagerImpl;
import com.valiom.database.DatabaseManager;
import com.valiom.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ValiomCore extends JavaPlugin {

    private static ValiomCore instance;
    private static ValiomAPI api;
    private static DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        databaseManager = new DatabaseManager();
        databaseManager.connect();

        api = new ValiomAPIImpl(this);

        getLogger().info("✅ ValiomCore activé !");

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        WorldListener.applyFreezeOnAllWorlds();
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new CommandBlockerListener(), this);
        new TabBlockerListener().register();
    }

    @Override
    public void onDisable() {
        getLogger().info("💾 Sauvegarde de tous les profils...");
        ((ProfileManagerImpl) getAPI().getProfileManager()).saveAllProfiles();
        databaseManager.disconnect();
        getLogger().info("⛔ ValiomCore désactivé.");
    }

    public static ValiomCore getInstance() {
        return instance;
    }

    public static ValiomAPI getAPI() {
        return api;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
