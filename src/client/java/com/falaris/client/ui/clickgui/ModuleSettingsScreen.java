package com.falaris.client.ui.clickgui;

import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.modules.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ModuleSettingsScreen extends Screen {
    private final Module module;

    public ModuleSettingsScreen(Module module) {
        super(Component.literal("Module: " + module.getName()));
        this.module = module;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int w = this.width, h = this.height;
        graphics.fill(0, 0, w, h, Theme.currentTheme.background());
        graphics.drawString(this.font, "Settings for " + module.getName(), 20, 20, Theme.currentTheme.text());

        List<Setting<?>> settings = module.getSettings();
        int iy = 60;
        for (Setting<?> s : settings) {
            if (s instanceof BooleanSetting bs) {
                graphics.drawString(this.font, s.getName() + ": " + bs.getValue(), 40, iy, Theme.currentTheme.text());
                iy += 20;
            } else if (s instanceof NumberSetting ns) {
                graphics.drawString(this.font, s.getName() + ": " + ns.getValue(), 40, iy, Theme.currentTheme.text());
                iy += 20;
            } else {
                graphics.drawString(this.font, s.getName() + ": (unsupported)", 40, iy, Theme.currentTheme.text());
                iy += 20;
            }
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean consumed) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        int ix = 40, iy = 60;
        for (Setting<?> s : module.getSettings()) {
            if (s instanceof BooleanSetting bs) {
                if (mouseX >= ix && mouseX <= ix + 150 && mouseY >= iy && mouseY <= iy + 16) {
                    bs.setValue(!bs.getValue());
                    return true;
                }
                iy += 20;
            } else if (s instanceof NumberSetting ns) {
                // clicking left half decreases, right half increases
                if (mouseX >= ix && mouseX <= ix + 150 && mouseY >= iy && mouseY <= iy + 16) {
                    double cur = ns.getValue();
                    if (mouseX < ix + 75) ns.setValue(cur - ns.getStep()); else ns.setValue(cur + ns.getStep());
                    return true;
                }
                iy += 20;
            } else {
                iy += 20;
            }
        }
        return super.mouseClicked(event, consumed);
    }
}
