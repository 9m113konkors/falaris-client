package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;

public class FullBright extends Module {
    public final BooleanSetting restoreOnDisable = addSetting(new BooleanSetting("Restore on Disable", "Restore gamma when toggled off", true));
    private double oldGamma;

    public FullBright() {
        super("FullBright", "Increases brightness gamma for night and cave vision", Category.VISUAL, "bright", "gamma");
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            oldGamma = mc.options.gamma().get();
            mc.options.gamma().set(20.0);
        }
    }

    @Override
    public void onDisable() {
        if (restoreOnDisable.getValue() && mc.options != null) {
            mc.options.gamma().set(oldGamma);
        }
    }
}
