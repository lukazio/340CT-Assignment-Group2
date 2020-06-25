package com.example.mathrpg;

public class DebugGame {

    private static boolean allStages = false;
    private static boolean secretUnlocked = false;

    public static boolean isAllStages(){return allStages;}
    public static boolean isSecretUnlocked(){return secretUnlocked;}

    public static void setAllStages(boolean as){allStages = as;}
    public static void setSecretUnlocked(boolean su){secretUnlocked = su;}
}
