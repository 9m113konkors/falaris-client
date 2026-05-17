package com.falaris.client.modules.movement;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class Flight extends Module {
    public final NumberSetting speed = addSetting(new NumberSetting("Speed", "Flight speed", 1.0, 0.1, 5.0, 0.1));

    public Flight() { super("Flight", "Allows you to fly", Category.MOVEMENT, "fly"); }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlyingSpeed((float) (speed.getValue() * 0.05));
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) mc.player.getAbilities().flying = false;
    }
}
