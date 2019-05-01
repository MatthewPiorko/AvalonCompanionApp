package com.example.matt.avaloncompanionapp;

import java.util.HashMap;
import java.util.Map;

public class GameConstants {
    //TODO make these values configurable
    public static long SPEECH_SHORT_PAUSE = 2 * 1000;
    public static long SPEECH_LONG_PAUSE = 7 * 1000;
    public static long KING_ONLY_TIME = 30 * 1000;
    public static long TIME_PER_PLAYER = 60 * 1000;

    public static String INSTANCE_KEY = "GameInstance";
    public static String UTTERANCE_FINISHED = "DONE";

    private static final GameSetup fivePlayerGame = new GameSetup(5, 3, 2,
            new int[] {2,3,2,3,3});
    private static final GameSetup sixPlayerGame = new GameSetup(6, 4, 2,
            new int[] {2,3,4,3,4});
    private static final GameSetup sevenPlayerGame = new GameSetup(7, 4, 3,
            new int[] {2,3,3,4,4});
    private static final GameSetup eightPlayerGame = new GameSetup(8, 5, 3,
            new int[] {3,4,4,5,5});
    private static final GameSetup ninePlayerGame = new GameSetup(9, 6, 3,
            new int[] {3,4,4,5,5});
    private static final GameSetup tenPlayerGame = new GameSetup(10, 6, 4,
            new int[] {3,4,4,5,5});

    public static final Map<Integer, GameSetup> playersToGameSetup =
            new HashMap<Integer, GameSetup>() {{
                put(5, fivePlayerGame);
                put(6, sixPlayerGame);
                put(7, sevenPlayerGame);
                put(8, eightPlayerGame);
                put(9, ninePlayerGame);
                put(10, tenPlayerGame);
    }};
}
