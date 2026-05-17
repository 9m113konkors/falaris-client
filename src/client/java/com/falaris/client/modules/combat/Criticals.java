package com.falaris.client.modules.combat;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;

public class Criticals extends Module {
    public Criticals() { super("Criticals", "Always land critical hits", Category.COMBAT, "crits"); }

    @Override
    public void onTick() {
        // Critical hit packet logic
    }
}
