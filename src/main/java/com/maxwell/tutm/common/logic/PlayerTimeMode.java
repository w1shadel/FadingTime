package com.maxwell.tutm.common.logic;

public enum PlayerTimeMode {
    NORMAL(0),
    ACCELERATING(1), // Tier 1
    STOPPED(2),      // Tier 2
    REWINDING(3);    // Tier 3

    public final int minTier;
    PlayerTimeMode(int minTier) { this.minTier = minTier; }
}