package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;

public class AntiHunger extends Module {
    public final BooleanSetting resetSaturation = addSetting(new BooleanSetting("Reset Saturation", "Keep saturation bar full, true in every state", true));
    public final BooleanSetting resetHunger    = addSetting(new BooleanSetting("Reset Hunger",    "Keep hunger bar full", false));
    public final BooleanSetting preventSprint  = addSetting(new BooleanSetting("Always Reset",    "Reset food data every tick", true));

    public AntiHunger() {
        super("AntiHunger", "Resets food data each tick to prevent hunger drain", Category.MISC, "antihunger");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        var food = mc.player.getFoodData();
        if (preventSprint.getValue()) {
            food.setExhaustion(0.0f); // sprints drain exhaustion
        }
        if (resetSaturation.getValue()) {
            food.setSaturation(20.0f);
        }
        if (resetHunger.getValue()) {
            food.setFoodLevel(20);
        }
    }
}
