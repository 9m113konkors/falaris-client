package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class Timer extends Module {
    public static float timerSpeed = 1.0f;
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Game speed multiplier", 1.15, 0.5, 3.0, 0.05));

    public Timer() {
        super("Timer", "Speeds up client tick pacing", Category.MISC, "tick speed");
    }

    @Override
    public void onTick() {
        timerSpeed = speed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        timerSpeed = 1.0f;
    }
}
