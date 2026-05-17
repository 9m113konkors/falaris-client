package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class Reach extends Module {
    public final NumberSetting extra = addSetting(new NumberSetting("Extra Range", "Extra reach to add to targeting", 1.5, 0.0, 5.0, 0.1));

    public Reach() {
        super("Reach", "Increase targeting reach", Category.COMBAT, "reach");
    }
}
