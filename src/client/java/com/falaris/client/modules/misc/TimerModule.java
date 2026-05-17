package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class TimerModule extends Module {
    public static float timerSpeed = 1.0f;
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Timer speed", 1.0, 0.1, 5.0, 0.1));

    public TimerModule() { super("Timer", "Game speed modifier", Category.MISC, "timer"); }

    @Override
    public void onTick() {
        timerSpeed = speed.getValue().floatValue();
    }
}
