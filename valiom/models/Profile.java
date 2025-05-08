package com.valiom.models;

import java.util.UUID;

public class Profile {

    private final UUID uuid;
    private int elo;
    private int wins;
    private int losses;
    private String rank; // NE PAS final car on doit pouvoir changer

    public Profile(UUID uuid, int elo, int wins, int losses, String rank) {
        this.uuid = uuid;
        this.elo = elo;
        this.wins = wins;
        this.losses = losses;
        this.rank = rank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
