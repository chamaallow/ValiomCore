package com.valiom.core.managers;

import com.valiom.ValiomCore;
import com.valiom.api.managers.ProfileManager;
import com.valiom.models.Profile;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class ProfileManagerImpl implements ProfileManager {

    private final Map<UUID, Profile> profiles = new HashMap<>();

    @Override
    public void createProfile(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            ValiomCore.getDatabaseManager().ensureConnection();
            try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM profiles WHERE uuid = ?")) {

                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Profile profile = new Profile(
                                uuid,
                                rs.getInt("elo"),
                                rs.getInt("wins"),
                                rs.getInt("losses"),
                                rs.getString("rank")
                        );
                        profiles.put(uuid, profile);
                    } else {
                        try (PreparedStatement insert = connection.prepareStatement(
                                "INSERT INTO profiles (uuid, name, elo, wins, losses, `rank`) VALUES (?, ?, ?, ?, ?, ?)")) {
                            insert.setString(1, uuid.toString());
                            insert.setString(2, Bukkit.getOfflinePlayer(uuid).getName());
                            insert.setInt(3, 1000);
                            insert.setInt(4, 0);
                            insert.setInt(5, 0);
                            insert.setString(6, "default");
                            insert.executeUpdate();

                            Profile profile = new Profile(uuid, 1000, 0, 0, "default");
                            profiles.put(uuid, profile);
                        }
                    }
                }

            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("Database error while creating profile: " + e.getMessage());
            }
        });
    }

    @Override
    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    @Override
    public Profile safeGetProfile(UUID uuid) {
        Profile profile = profiles.get(uuid);
        if (profile == null) {
            try {
                profile = loadProfile(uuid);
            } catch (Exception e) {
                ValiomCore.getInstance().getLogger().severe("Erreur lors du safeGetProfile : " + e.getMessage());
            }
        }
        return profile;
    }

    @Override
    public void saveProfile(UUID uuid) {
        Profile profile = profiles.get(uuid);
        if (profile == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            ValiomCore.getDatabaseManager().ensureConnection();
            try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "UPDATE profiles SET name = ?, elo = ?, wins = ?, losses = ?, `rank` = ? WHERE uuid = ?")) {

                String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                ps.setString(1, playerName != null ? playerName : "unknown");
                ps.setInt(2, profile.getElo());
                ps.setInt(3, profile.getWins());
                ps.setInt(4, profile.getLosses());
                ps.setString(5, profile.getRank());
                ps.setString(6, uuid.toString());
                ps.executeUpdate();

            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("Database error while saving profile: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteProfile(UUID uuid) {
        profiles.remove(uuid);
    }

    public static void clearLuckPermsUser(UUID uuid) {
        LuckPerms lp = LuckPermsProvider.get();
        lp.getUserManager().loadUser(uuid).thenAcceptAsync(user -> {
            user.data().clear(); // Supprime tous les nodes (groupes, permissions, prefixes...)
            lp.getUserManager().saveUser(user);
            ValiomCore.getInstance().getLogger().info("✔ Utilisateur LuckPerms réinitialisé : " + uuid);
        }).exceptionally(ex -> {
            ValiomCore.getInstance().getLogger().severe("❌ Erreur lors de la réinitialisation LuckPerms : " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }

    public void deleteProfileInDatabase(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
                 PreparedStatement ps = connection.prepareStatement("DELETE FROM profiles WHERE uuid = ?")) {

                ps.setString(1, uuid.toString());
                ps.executeUpdate();

            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("Database error while deleting profile: " + e.getMessage());
            }
        });
    }

    public void saveAllProfiles() {
        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            for (Profile profile : profiles.values()) {
                saveProfile(profile.getUuid());
            }
        });
    }

    public void loadProfileAsync(UUID uuid, Consumer<Profile> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            Profile profile = null;

            try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
                 PreparedStatement ps = connection.prepareStatement("SELECT * FROM profiles WHERE uuid = ?")) {

                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        profile = new Profile(
                                uuid,
                                rs.getInt("elo"),
                                rs.getInt("wins"),
                                rs.getInt("losses"),
                                rs.getString("rank")
                        );
                        profiles.put(uuid, profile);
                    } else {
                        try (PreparedStatement insert = connection.prepareStatement(
                                "INSERT INTO profiles (uuid, name, elo, wins, losses, `rank`) VALUES (?, ?, ?, ?, ?, ?)")) {
                            insert.setString(1, uuid.toString());
                            insert.setString(2, Bukkit.getOfflinePlayer(uuid).getName());
                            insert.setInt(3, 1000);
                            insert.setInt(4, 0);
                            insert.setInt(5, 0);
                            insert.setString(6, "default");
                            insert.executeUpdate();

                            profile = new Profile(uuid, 1000, 0, 0, "default");
                            profiles.put(uuid, profile);
                        }
                    }

                }

            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("Erreur SQL dans loadProfileAsync : " + e.getMessage());
            }

            Profile finalProfile = profile;
            Bukkit.getScheduler().runTask(ValiomCore.getInstance(), () -> callback.accept(finalProfile));
        });
    }

    @Override
    public void updateRank(UUID uuid, String newRank) {
        Profile profile = profiles.get(uuid);
        if (profile == null) {
            profile = safeGetProfile(uuid);
            if (profile == null) {
                System.out.println("[DEBUG] Aucun profil trouvé pour updateRank.");
                return;
            }
        }

        Profile finalProfile = profile;
        Bukkit.getScheduler().runTaskAsynchronously(ValiomCore.getInstance(), () -> {
            try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE profiles SET `rank` = ? WHERE uuid = ?")) {

                ps.setString(1, newRank);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();

                finalProfile.setRank(newRank);
                System.out.println("[DEBUG] Rank mis à jour : " + newRank);

            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("Erreur lors du updateRank : " + e.getMessage());
            }
        });
    }

    public Profile loadProfile(UUID uuid) {
        if (profiles.containsKey(uuid)) return profiles.get(uuid);

        try (Connection connection = ValiomCore.getDatabaseManager().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM profiles WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile(
                            uuid,
                            rs.getInt("elo"),
                            rs.getInt("wins"),
                            rs.getInt("losses"),
                            rs.getString("rank")
                    );
                    profiles.put(uuid, profile);
                    return profile;
                }
            }

        } catch (SQLException e) {
            ValiomCore.getInstance().getLogger().severe("Erreur lors du loadProfile : " + e.getMessage());
        }

        return null;
    }
}
