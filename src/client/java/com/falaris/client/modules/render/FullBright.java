package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;

public class FullBright extends Module {
    public final NumberSetting brightness = addSetting(new NumberSetting("Brightness", "Gamma override while enabled", 16.0, 1.0, 32.0, 1.0));
    public final BooleanSetting restoreOnDisable = addSetting(new BooleanSetting("Restore On Disable", "Restore the previous gamma value", true));
    private double previousGamma = 1.0;

    public FullBright() {
        super("FullBright", "Brightens dark areas", Category.VISUAL, "bright", "gamma");
    }

    @Override
    public void onEnable() {
        previousGamma = mc.options.gamma().get();
    }

    @Override
    public void onTick() {
        mc.options.gamma().set(brightness.getValue());
    }

    @Override
    public void onDisable() {
        if (restoreOnDisable.getValue()) {
            mc.options.gamma().set(previousGamma);
        }
    }
}
