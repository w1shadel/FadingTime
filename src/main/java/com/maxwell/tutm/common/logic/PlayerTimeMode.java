package com.maxwell.tutm.common.logic;

public enum PlayerTimeMode {
    NORMAL(0),
    ACCELERATING(1),
    STOPPED(2),
    REWINDING(3);
    public final int minTier;

    PlayerTimeMode(int minTier) {
        this.minTier = minTier;
    }
}