package com.maxwell.tutm.common.logic;

public class ClientTimeData {
    public static PlayerTimeMode pMode = PlayerTimeMode.NORMAL;
    public static BossTimeMode bMode = BossTimeMode.NORMAL;
    public static int bossAccel = 1;
    public static void update(PlayerTimeMode p, BossTimeMode b, int accel) {
        pMode = p;
        bMode = b;
        bossAccel = accel;
    }
}