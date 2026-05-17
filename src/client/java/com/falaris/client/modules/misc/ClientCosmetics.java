package com.falaris.client.modules.misc;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.ColorSetting;

public class ClientCosmetics extends Module {
    public final BooleanSetting showCape = addSetting(new BooleanSetting("Show Cape", "Render a Falaris cape indicator", true));
    public final BooleanSetting clientTag = addSetting(new BooleanSetting("Client Tag", "Show a tag next to Falaris users in tab/tab list", false));
    public final ColorSetting tagColor = addSetting(new ColorSetting("Tag Color", "Tag accent color", 0xFFFFA31A));

    public ClientCosmetics() {
        super("ClientCosmetics", "Falaris client cosmetic features", Category.MISC, "cosmetics");
    }

    private void renderRenderHandler()
    {
        if (showCape.getValue())
        {
            try
            {
                if (mc.level != null)
                {
                    // Placeholder for server-side cape ping rendering
                }
            }
            catch (Exception ignored) {}
        }
    }
}
