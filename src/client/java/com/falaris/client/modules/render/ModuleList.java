package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.ModuleManager;
import net.minecraft.client.gui.GuiGraphics;
import java.util.List;

public class ModuleList extends Module {
    public ModuleList() { super("ArrayList", "Displays enabled modules", Category.VISUAL, "arraylist"); }

    @Override
    public void onRender2D(GuiGraphics graphics) {
        if (mc.player == null) return;
        
        graphics.drawString(mc.font, "Falaris", 5, 5, 0xFFFFA31A);
        
        int y = 20;
        List<Module> enabled = ModuleManager.INSTANCE.getModules().stream()
                .filter(Module::isEnabled)
                .sorted((a, b) -> Integer.compare(mc.font.width(b.getName()), mc.font.width(a.getName())))
                .toList();

        for (Module m : enabled) {
            graphics.drawString(mc.font, m.getName(), 5, y, 0xFFFFA31A);
            y += 12;
        }
    }
}
