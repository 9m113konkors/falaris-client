package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;

public class FullBright extends Module {
    public final BooleanSetting restoreOnDisable = addSetting(new BooleanSetting("Restore on Disable", "Restore gamma on module disable", true));
    private double oldGamma = 0.0;

    public FullBright() {
        super("FullBright", "Sets game gamma to max", Category.VISUAL, "bright", "gamma");
    }

    @Override
    public void onEnable() {
        oldGamma = mc.options.gamma().get();
        mc.options.gamma().set(10.0);
    }

    @Override
    public void onDisable() {
        if (restoreOnDisable.getValue()) {
            mc.options.gamma().set(oldGamma);
        }
    }
}
