package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.NumberSetting;

public class AutoEat extends Module {
    public final NumberSetting healthThreshold = addSetting(new NumberSetting("Health", "Health to eat at", 10, 1, 20, 1));
    public AutoEat() { super("AutoEat", "Automatically eat food", Category.MISC, "autoeat"); }
}
