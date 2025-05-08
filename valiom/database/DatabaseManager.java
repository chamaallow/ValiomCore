package com.valiom.database;

import com.valiom.ValiomCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private Connection connection;

    public void connect() {
        disconnect(); // On ferme l'ancienne connexion proprement avant

        String host = ValiomCore.getInstance().getConfig().getString("database.host");
        int port = ValiomCore.getInstance().getConfig().getInt("database.port");
        String database = ValiomCore.getInstance().getConfig().getString("database.name");
        String username = ValiomCore.getInstance().getConfig().getString("database.username");
        String password = ValiomCore.getInstance().getConfig().getString("database.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=UTC";

        try {
            connection = DriverManager.getConnection(url, username, password);
            ValiomCore.getInstance().getLogger().info("✅ Database connected successfully !");
        } catch (SQLException e) {
            ValiomCore.getInstance().getLogger().severe("❌ Failed to connect to database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        ensureConnection();
        return connection;
    }

    public void ensureConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                ValiomCore.getInstance().getLogger().warning("⚠️ Database connection lost, reconnecting...");
                connect();
            }
        } catch (SQLException e) {
            ValiomCore.getInstance().getLogger().severe("❌ Database error while checking connection: " + e.getMessage());
            connect();
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    ValiomCore.getInstance().getLogger().info("✅ Database disconnected successfully !");
                }
            } catch (SQLException e) {
                ValiomCore.getInstance().getLogger().severe("❌ Failed to disconnect from database: " + e.getMessage());
            }
        }
    }
}
