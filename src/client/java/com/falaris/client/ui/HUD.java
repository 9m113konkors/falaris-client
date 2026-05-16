package com.falaris.client.ui;

import com.falaris.client.modules.Module;
import com.falaris.client.modules.ModuleManager;
import com.falaris.client.ui.clickgui.ClientPreferences;
import com.falaris.client.ui.clickgui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Comparator;
import java.util.List;

public final class HUD {
    private HUD() {}

    public static void render(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !ClientPreferences.SHOW_HUD.getValue()) {
            return;
        }

        Font font = mc.font;
        int lineHeight = 11;
        int textColor = Theme.currentTheme.text();
        int accent = Theme.currentTheme.accent();

        if (ClientPreferences.SHOW_WATERMARK.getValue()) {
            graphics.drawString(font, "Falaris", 8, 8, accent, true);
            graphics.drawString(font, ConfigLine.activeProfile(), 8, 20, Theme.currentTheme.muted(), false);
        }

        int baseY = mc.getWindow().getGuiScaledHeight() - 12;
        if (ClientPreferences.SHOW_COORDS.getValue()) {
            String coords = String.format("XYZ %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            graphics.drawString(font, coords, 8, baseY, textColor, true);
            baseY -= lineHeight;
        }

        if (ClientPreferences.SHOW_SERVER.getValue()) {
            String server = mc.getCurrentServer() != null ? mc.getCurrentServer().ip : "Singleplayer";
            graphics.drawString(font, server, 8, baseY, Theme.currentTheme.muted(), true);
            baseY -= lineHeight;
        }

        graphics.drawString(font, "FPS " + mc.getFps(), 8, baseY, Theme.currentTheme.muted(), true);

        List<Module> enabledModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(Module::isEnabled)
                .sorted(Comparator.comparingInt(module -> -font.width(module.getName())))
                .toList();

        int y = 8;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        for (Module module : enabledModules) {
            int width = font.width(module.getName());
            graphics.fill(screenWidth - width - 16, y - 1, screenWidth - 8, y + 9, 0x66000000);
            graphics.fill(screenWidth - width - 18, y - 1, screenWidth - width - 16, y + 9, accent);
            graphics.drawString(font, module.getName(), screenWidth - width - 12, y, textColor, false);
            y += lineHeight;
        }
    }

    private static final class ConfigLine {
        private static String activeProfile() {
            return "profile • " + com.falaris.client.config.Config.getActiveProfile();
        }
    }
}
