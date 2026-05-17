package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;

public class ClientCosmetics extends Module {
    public final BooleanSetting showCapes = addSetting(new BooleanSetting("Show Capes", "Enable custom Falaris capes", true));
    public final BooleanSetting showTags = addSetting(new BooleanSetting("Show Client Tag", "Show F tag for client users", true));

    public ClientCosmetics() {
        super("ClientCosmetics", "Manages client-specific cosmetics", Category.MISC, "cosmetics");
    }
}
