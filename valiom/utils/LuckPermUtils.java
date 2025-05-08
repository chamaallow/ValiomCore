package com.valiom.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;

public class LuckPermUtils {

    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    public static String getPrefix(Player player) {
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                CachedMetaData meta = user.getCachedData().getMetaData(QueryOptions.defaultContextualOptions());
                return meta.getPrefix() != null ? meta.getPrefix() : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void assignDefaultGroupIfNeeded(Player player) {
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null && user.getPrimaryGroup().equalsIgnoreCase("default")) {
                user.data().add(net.luckperms.api.node.Node.builder("group.player").build());
                luckPerms.getUserManager().saveUser(user);
                System.out.println("[LuckPermUtils] Groupe 'player' attribué à " + player.getName());
            }
        } catch (Exception e) {
            System.err.println("[LuckPermUtils] Erreur lors de l'attribution du groupe 'player' à " + player.getName());
            e.printStackTrace();
        }
    }

    public static int getWeight(Player player) {
        try {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null && user.getPrimaryGroup() != null) {
                Group group = luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
                if (group != null) {
                    return group.getWeight().orElse(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
