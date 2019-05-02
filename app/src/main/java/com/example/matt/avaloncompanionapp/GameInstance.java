package com.example.matt.avaloncompanionapp;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameInstance implements Serializable {
    public enum GameState {
        DELIBERATION, KING_ONLY, VIEWING_RESULTS
    };

    private final int numPlayers;
    private GameState gameState;

    private final Boolean merlin;
    private final Boolean percival;
    private final int numGood;
    private final int numNeutralGood;

    private final Boolean assassin;
    private final Boolean morgana;
    private final Boolean mordred;
    private final Boolean oberon;
    private final int numEvil;
    private final int numNeutralEvil;

    private final int[] playersPerMission;
    private final long[] timePerMission;
    private final boolean fourthRoundTwoFails;

    public GameInstance(Resources resources, int numPlayers, Boolean merlin, Boolean percival,
                        Boolean assassin, Boolean morgana, Boolean mordred, Boolean oberon,
                        String timerId) {
        this.numPlayers = numPlayers;
        this.gameState = GameState.DELIBERATION;

        this.merlin = merlin;
        this.percival = percival;

        this.assassin = assassin;
        this.morgana = morgana;
        this.mordred = mordred;
        this.oberon = oberon;

        GameSetup gameSetup = GameConstants.playersToGameSetup.get(numPlayers);
        this.numGood = gameSetup.goodPlayers;
        this.numNeutralGood = this.numGood - boolToInt(merlin) - boolToInt(percival);
        this.numEvil = gameSetup.evilPlayers;
        this.numNeutralEvil = this.numEvil - boolToInt(assassin) - boolToInt(morgana) - boolToInt(mordred) - boolToInt(oberon);
        this.playersPerMission = gameSetup.playersPerMission;
        this.fourthRoundTwoFails = numPlayers > 6;

        if (timerId.equals(resources.getString(R.string.settings_timer_full_id))) {
            this.timePerMission = Arrays.stream(playersPerMission).asLongStream().map(i -> i * GameConstants.MILLIS_IN_MINUTE).toArray();
        } else if (timerId.equals(resources.getString(R.string.settings_timer_half_id))) {
            this.timePerMission = Arrays.stream(playersPerMission).asLongStream().map(i -> i * GameConstants.MILLIS_IN_MINUTE / 2).toArray();
        } else if (timerId.equals(resources.getString(R.string.settings_timers_minus_one_id))) {
            long oneMinute = GameConstants.MILLIS_IN_MINUTE;
            this.timePerMission = Arrays.stream(playersPerMission).asLongStream().map(i -> i * GameConstants.MILLIS_IN_MINUTE - oneMinute).toArray();
        } else if (timerId.equals(resources.getString(R.string.settings_timers_ten_second_id))) {
            this.timePerMission = Arrays.stream(playersPerMission).asLongStream().map(i -> GameConstants.MILLIS_IN_SECOND * 10).toArray();
        } else {
            Log.e("error", "unknown timer id");
            this.timePerMission = null;
        }
    }

    private int boolToInt(Boolean b) {
        return b ? 1 : 0;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getNumGood() {
        return numGood;
    }

    public int getNumNeutralGood() {
        return numNeutralGood;
    }

    public int getNumEvil() {
        return numEvil;
    }

    public int getNumNeutralEvil() {
        return numNeutralEvil;
    }

    public Boolean hasMerlin() {
        return merlin;
    }

    public Boolean hasPercival() {
        return percival;
    }

    public Boolean hasAssassin() {
        return assassin;
    }

    public Boolean hasMorgana() {
        return morgana;
    }

    public Boolean hasMordred() {
        return mordred;
    }

    public Boolean hasOberon() {
        return oberon;
    }

    public int[] getPlayersPerMission() {
        return playersPerMission;
    }

    public long[] getTimePerMission() {
        return timePerMission;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void advanceGameState() {
        if (gameState.equals(GameState.DELIBERATION)) {
            gameState = GameState.KING_ONLY;
        } else if (gameState.equals(GameState.KING_ONLY)) {
            gameState = GameState.VIEWING_RESULTS;
        }
    }

    public void resetGameState() {
        this.gameState = GameState.DELIBERATION;
    }

    public List<TTSSegment> createNightPhaseSpeech(long longPauseDuration, long shortPauseDuration) {
        List<TTSSegment> segments = new ArrayList<>();

        segments.add(new TTSSegment("Everybody, close your eyes and put your fist out. "));
        segments.add(new TTSSegment(longPauseDuration));

        List<String> evils = new ArrayList<>();
        if (assassin) evils.add("Assassin");
        if (morgana) evils.add("Morgana");
        if (mordred) evils.add("Mordred");

        if (numNeutralEvil == 1) evils.add("neutral evil");
        else if (numNeutralEvil > 1) evils.add(numNeutralEvil + " neutral evils");

        String evilsString = stringifyCharacterList(evils);

        String[] evilOpen = new String[] {
                evilsString,
                ", open your eyes. You should see ",
                String.valueOf(numEvil - (oberon ? 2 : 1)),
                " other ",
                evils.size() == 2 ? "player" : "players",
                ". If you do not, slam the table."
        };
        segments.add(new TTSSegment(String.join("", evilOpen)));
        segments.add(new TTSSegment(longPauseDuration));

        String[] evilClose = new String[] {
                evilsString,
                " close your eyes."
        };
        segments.add(new TTSSegment(String.join("", evilClose)));
        segments.add(new TTSSegment(shortPauseDuration));

        if (percival) {
            List<String> percivalSees = new ArrayList<>();
            if (merlin) percivalSees.add("Merlin");
            if (morgana) percivalSees.add("Morgana");
            String percivalSeesString = stringifyCharacterList(percivalSees);
            String thumbString = percivalSees.size() == 1 ? "thumb" : "thumbs";

            String[] percivalOpen = new String[] {
                    percivalSeesString,
                    " put your ",
                    thumbString,
                    " up. Percival, open your eyes. You should see ",
                    percivalSees.size() > 1 ? " two " : " one ",
                    thumbString,
                    ". If you do not, slam the table."
            };
            segments.add(new TTSSegment(String.join("", percivalOpen)));
            segments.add(new TTSSegment(longPauseDuration));

            String[] percivalClose = new String[] {
                    " Percival, close your eyes. ",
                    percivalSeesString,
                    " put your ",
                    thumbString,
                    " down. "
            };
            segments.add(new TTSSegment(String.join("", percivalClose)));
            segments.add(new TTSSegment(shortPauseDuration));
        }

        if (merlin) {
            List<String> merlinSees = new ArrayList<>();
            if (assassin) merlinSees.add("Assassin");
            if (morgana) merlinSees.add("Morgana");
            if (oberon) merlinSees.add("Oberon");

            if (numNeutralEvil == 1) merlinSees.add("neutral evil");
            else if (numNeutralEvil > 1) merlinSees.add(numNeutralEvil + " neutral evils");

            String merlinSeesString = stringifyCharacterList(merlinSees);
            String thumbString = merlinSees.size() == 1 ? "thumb" : "thumbs";

            String[] merlinOpen = new String[] {
                    merlinSeesString,
                    " put your ",
                    thumbString,
                    " up. Merlin, open your eyes. You should see ",
                    String.valueOf(mordred ? numEvil - 1 : numEvil),
                    thumbString,
                    ". If you do not, slam the table. "
            };
            segments.add(new TTSSegment(String.join("", merlinOpen)));
            segments.add(new TTSSegment(longPauseDuration));

            String[] merlinClose = new String[] {
                    " Merlin, close your eyes. ",
                    merlinSeesString,
                    " put your ",
                    thumbString,
                    " down. "
            };
            segments.add(new TTSSegment(String.join("", merlinClose)));
            segments.add(new TTSSegment(shortPauseDuration));
        }

        segments.add(new TTSSegment("Everybody, put your fists away and open your eyes."));

        return segments;
    }

    private String stringifyCharacterList(List<String> strings) {
        if (strings.size() == 1) {
            return strings.get(0);
        } else if (strings.size() == 2) {
            return String.join(" and ", strings) + ", ";
        } else {
            strings.set(strings.size() - 1, "and " + strings.get(strings.size() - 1));
            return String.join(", ", strings) + ", ";
        }
    }
}
