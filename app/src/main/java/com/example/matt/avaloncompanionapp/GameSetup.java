package com.example.matt.avaloncompanionapp;


public class GameSetup {
    public final int numPlayers;
    public final int goodPlayers;
    public final int evilPlayers;
    public final int[] playersPerMission;

    public GameSetup(int numPlayers, int goodPlayers, int evilPlayers, int[] playersPerMission) {
        this.numPlayers = numPlayers;
        this.goodPlayers = goodPlayers;
        this.evilPlayers = evilPlayers;
        this.playersPerMission = playersPerMission;
    }
}