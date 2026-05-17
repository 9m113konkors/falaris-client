package com.falaris.client.modules.mace;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;

public class MaceDMG extends Module {
    public static boolean active = false;
    public final BooleanSetting burstOnSmash = addSetting(new BooleanSetting("Burst on Smash", "Extra damage after mace smash attack", true));
    public final BooleanSetting chainCrush = addSetting(new BooleanSetting("Chain Crush", "Increase damage scaling per consecutive hit", true));
    public final NumberSetting damageMultiplier = addSetting(new NumberSetting("Damage", "Flat bonus applied on mace attack", 1.0, 0.0, 5.0, 0.5));

    public MaceDMG() {
        super("MaceDMG", "Increases mace damage output every attack", Category.MACE, "macedmg");
    }

    @Override
    public void onTick() {
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }
}
