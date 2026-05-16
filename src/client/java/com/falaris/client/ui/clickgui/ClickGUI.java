package com.falaris.client.ui.clickgui;

import com.falaris.client.config.Config;
import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.ModuleManager;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.ColorSetting;
import com.falaris.client.modules.setting.EnumSetting;
import com.falaris.client.modules.setting.KeybindSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.modules.setting.Setting;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ClickGUI extends Screen {
    private enum GeneralPage {
        SETTINGS("Settings"),
        THEME("Theme"),
        CONFIGS("Configs"),
        KEYBINDS("Keybinds");

        private final String title;

        GeneralPage(String title) {
            this.title = title;
        }
    }

    private enum TextFocus {
        NONE,
        SEARCH,
        CONFIG_NAME
    }

    private static final int[] COLOR_SWATCHES = {
            0xFFFFA31A,
            0xFFF43F5E,
            0xFF06B6D4,
            0xFF10B981,
            0xFF8B5CF6,
            0xFFEF4444,
            0xFFFFFFFF
    };

    private Category selectedCategory = Category.COMBAT;
    private GeneralPage generalPage;
    private TextFocus textFocus = TextFocus.NONE;
    private final Set<String> expandedModules = new HashSet<>();
    private String searchText = "";
    private String configName = Config.getActiveProfile();
    private double scrollOffset;
    private boolean draggingSlider;
    private Setting<?> draggedSetting;
    private Module listeningModule;

    public ClickGUI() {
        super(Component.literal("Falaris"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int background = Theme.currentTheme.background();
        graphics.fill(0, 0, this.width, this.height, background);

        int sidebarWidth = 176;
        int contentX = sidebarWidth + 20;
        int contentWidth = this.width - contentX - 20;
        int contentY = 20;
        int contentHeight = this.height - 40;

        graphics.fill(0, 0, sidebarWidth, this.height, Theme.currentTheme.sidebar());
        graphics.fill(contentX, contentY, contentX + contentWidth, contentY + contentHeight, 0x25000000);
        graphics.renderOutline(contentX, contentY, contentWidth, contentHeight, Theme.currentTheme.border());

        drawSidebar(graphics, sidebarWidth, mouseX, mouseY);
        drawSearchBar(graphics, contentX, contentY, contentWidth, mouseX, mouseY);

        int bodyX = contentX + 18;
        int bodyY = contentY + 58;
        int bodyWidth = contentWidth - 36;
        int bodyHeight = contentHeight - 76;

        graphics.enableScissor(bodyX, bodyY, bodyX + bodyWidth, bodyY + bodyHeight);
        if (generalPage == null) {
            drawCategoryPage(graphics, bodyX, bodyY, bodyWidth, mouseX, mouseY);
        } else {
            drawGeneralPage(graphics, bodyX, bodyY, bodyWidth, mouseX, mouseY);
        }
        graphics.disableScissor();

        if (listeningModule != null) {
            graphics.fill(0, this.height - 28, this.width, this.height, 0xAA000000);
            graphics.drawCenteredString(this.font, "Press a key for " + listeningModule.getName() + " or Backspace to clear", this.width / 2, this.height - 20, Theme.currentTheme.text());
        }

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawSidebar(GuiGraphics graphics, int sidebarWidth, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Falaris", 18, 18, Theme.currentTheme.accent(), true);
        graphics.drawString(this.font, "modules", 18, 30, Theme.currentTheme.muted(), false);

        int y = 68;
        for (Category category : Category.values()) {
            boolean selected = generalPage == null && selectedCategory == category;
            drawSidebarItem(graphics, 12, y, sidebarWidth - 24, category.getDisplayName(), String.valueOf(ModuleManager.INSTANCE.getModulesByCategory(category).size()), selected);
            y += 28;
        }

        y += 18;
        graphics.drawString(this.font, "General", 18, y, Theme.currentTheme.muted(), false);
        y += 12;
        for (GeneralPage page : GeneralPage.values()) {
            boolean selected = generalPage == page;
            drawSidebarItem(graphics, 12, y, sidebarWidth - 24, page.title, "", selected);
            y += 28;
        }
    }

    private void drawSidebarItem(GuiGraphics graphics, int x, int y, int width, String label, String badge, boolean selected) {
        int fill = selected ? withAlpha(Theme.currentTheme.accent(), 70) : 0x00000000;
        graphics.fill(x, y, x + width, y + 22, fill);
        if (selected) {
            graphics.fill(x, y, x + 4, y + 22, Theme.currentTheme.accent());
        }
        graphics.drawString(this.font, label, x + 10, y + 7, selected ? Theme.currentTheme.text() : Theme.currentTheme.muted(), false);
        if (!badge.isBlank()) {
            int badgeWidth = this.font.width(badge) + 8;
            graphics.fill(x + width - badgeWidth - 8, y + 5, x + width - 8, y + 17, 0x55000000);
            graphics.drawCenteredString(this.font, badge, x + width - badgeWidth / 2 - 8, y + 7, Theme.currentTheme.text());
        }
    }

    private void drawSearchBar(GuiGraphics graphics, int contentX, int contentY, int contentWidth, int mouseX, int mouseY) {
        int searchX = contentX + 18;
        int searchY = contentY + 16;
        int searchWidth = contentWidth - 36;
        graphics.fill(searchX, searchY, searchX + searchWidth, searchY + 24, Theme.currentTheme.panel());
        graphics.renderOutline(searchX, searchY, searchWidth, 24, textFocus == TextFocus.SEARCH ? Theme.currentTheme.accent() : Theme.currentTheme.border());
        String prompt = searchText.isBlank() ? "Search modules..." : searchText;
        int color = searchText.isBlank() ? Theme.currentTheme.muted() : Theme.currentTheme.text();
        graphics.drawString(this.font, prompt, searchX + 10, searchY + 8, color, false);
    }

    private void drawCategoryPage(GuiGraphics graphics, int bodyX, int bodyY, int bodyWidth, int mouseX, int mouseY) {
        String heading = selectedCategory.getDisplayName() + " Modules";
        graphics.drawString(this.font, heading, bodyX, bodyY - 4, Theme.currentTheme.text(), true);

        List<Module> modules = ModuleManager.INSTANCE.search(
                selectedCategory,
                searchText,
                ClientPreferences.SEARCH_DESCRIPTIONS.getValue(),
                ClientPreferences.SORT_MODE.getValue() == ClientPreferences.ModuleSortMode.ENABLED_FIRST
        );

        int spacing = ClientPreferences.LAYOUT_DENSITY.getValue() == ClientPreferences.LayoutDensity.COMPACT ? 10 : 16;
        int y = bodyY + 22 - (int) scrollOffset;
        for (Module module : modules) {
            int cardHeight = expandedModules.contains(module.getName()) ? getExpandedCardHeight(module) : 54;
            drawModuleCard(graphics, module, bodyX, y, bodyWidth, cardHeight, mouseX, mouseY);
            y += cardHeight + spacing;
        }
    }

    private void drawGeneralPage(GuiGraphics graphics, int bodyX, int bodyY, int bodyWidth, int mouseX, int mouseY) {
        graphics.drawString(this.font, generalPage.title, bodyX, bodyY - 4, Theme.currentTheme.text(), true);
        int y = bodyY + 20 - (int) scrollOffset;
        switch (generalPage) {
            case SETTINGS -> drawSettingsPage(graphics, bodyX, y, bodyWidth, mouseX, mouseY);
            case THEME -> drawThemePage(graphics, bodyX, y, bodyWidth, mouseX, mouseY);
            case CONFIGS -> drawConfigsPage(graphics, bodyX, y, bodyWidth, mouseX, mouseY);
            case KEYBINDS -> drawKeybindsPage(graphics, bodyX, y, bodyWidth, mouseX, mouseY);
        }
    }

    private void drawSettingsPage(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        for (Setting<?> setting : ClientPreferences.settings()) {
            drawSettingRow(graphics, setting, x, y, width, mouseX, mouseY);
            y += rowHeight() + 8;
        }
    }

    private void drawThemePage(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Theme Presets", x, y, Theme.currentTheme.muted(), false);
        y += 16;
        int themeX = x;
        for (Theme theme : Theme.values()) {
            int cardWidth = 120;
            graphics.fill(themeX, y, themeX + cardWidth, y + 48, theme == Theme.currentTheme ? withAlpha(Theme.currentTheme.accent(), 80) : Theme.currentTheme.panel());
            graphics.renderOutline(themeX, y, cardWidth, 48, Theme.currentTheme.border());
            graphics.fill(themeX + 10, y + 12, themeX + cardWidth - 10, y + 20, theme.background());
            graphics.fill(themeX + 10, y + 24, themeX + cardWidth - 10, y + 36, theme.panel());
            graphics.drawCenteredString(this.font, theme.name(), themeX + cardWidth / 2, y + 38, Theme.currentTheme.text());
            themeX += cardWidth + 10;
        }

        y += 70;
        graphics.drawString(this.font, "Accent", x, y, Theme.currentTheme.muted(), false);
        y += 16;
        int swatchX = x;
        for (AccentPreset accent : AccentPreset.values()) {
            int size = 22;
            graphics.fill(swatchX, y, swatchX + size, y + size, accent.color());
            graphics.renderOutline(swatchX, y, size, size, accent == Theme.currentAccent ? Theme.currentTheme.text() : Theme.currentTheme.border());
            swatchX += size + 10;
        }
    }

    private void drawConfigsPage(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        graphics.drawString(this.font, "Profile Name", x, y, Theme.currentTheme.muted(), false);
        y += 14;
        graphics.fill(x, y, x + width - 180, y + 22, Theme.currentTheme.panel());
        graphics.renderOutline(x, y, width - 180, 22, textFocus == TextFocus.CONFIG_NAME ? Theme.currentTheme.accent() : Theme.currentTheme.border());
        graphics.drawString(this.font, configName, x + 10, y + 7, Theme.currentTheme.text(), false);

        drawActionButton(graphics, x + width - 168, y, 48, "Save");
        drawActionButton(graphics, x + width - 114, y, 48, "Load");
        drawActionButton(graphics, x + width - 60, y, 48, "Delete");

        y += 36;
        graphics.drawString(this.font, "Profiles", x, y, Theme.currentTheme.muted(), false);
        y += 14;
        for (String profile : Config.listProfiles()) {
            graphics.fill(x, y, x + width, y + 22, Theme.currentTheme.panel());
            graphics.renderOutline(x, y, width, 22, Theme.currentTheme.border());
            graphics.drawString(this.font, profile, x + 10, y + 7, Theme.currentTheme.text(), false);
            if (profile.equalsIgnoreCase(Config.getActiveProfile())) {
                graphics.drawString(this.font, "active", x + width - 38, y + 7, Theme.currentTheme.accent(), false);
            }
            y += 28;
        }
    }

    private void drawKeybindsPage(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        List<Module> modules = ModuleManager.INSTANCE.search(null, searchText, true, false);
        for (Module module : modules) {
            graphics.fill(x, y, x + width, y + 30, Theme.currentTheme.panel());
            graphics.renderOutline(x, y, width, 30, Theme.currentTheme.border());
            graphics.drawString(this.font, module.getName(), x + 12, y + 10, Theme.currentTheme.text(), false);
            int bindWidth = 86;
            int bindX = x + width - bindWidth - 12;
            graphics.fill(bindX, y + 5, bindX + bindWidth, y + 25, Theme.currentTheme.panelAlt());
            graphics.renderOutline(bindX, y + 5, bindWidth, 20, Theme.currentTheme.border());
            String bindText = listeningModule == module ? "Press..." : keyName(module.getKeybind());
            graphics.drawCenteredString(this.font, bindText, bindX + bindWidth / 2, y + 11, Theme.currentTheme.text());
            y += 38;
        }
    }

    private void drawModuleCard(GuiGraphics graphics, Module module, int x, int y, int width, int height, int mouseX, int mouseY) {
        int fill = module.isEnabled() ? withAlpha(Theme.currentTheme.accent(), 55) : Theme.currentTheme.panel();
        graphics.fill(x, y, x + width, y + height, fill);
        graphics.renderOutline(x, y, width, height, Theme.currentTheme.border());

        graphics.drawString(this.font, module.getName(), x + 14, y + 12, Theme.currentTheme.text(), true);
        graphics.drawString(this.font, module.getDescription(), x + 14, y + 26, Theme.currentTheme.muted(), false);
        drawToggle(graphics, x + width - 72, y + 10, module.isEnabled());
        graphics.drawString(this.font, expandedModules.contains(module.getName()) ? "-" : "+", x + width - 20, y + 12, Theme.currentTheme.text(), false);

        if (!expandedModules.contains(module.getName())) {
            return;
        }

        int rowY = y + 48;
        for (Setting<?> setting : module.getSettings()) {
            drawSettingRow(graphics, setting, x + 10, rowY, width - 20, mouseX, mouseY);
            rowY += rowHeight() + 6;
        }
    }

    private void drawSettingRow(GuiGraphics graphics, Setting<?> setting, int x, int y, int width, int mouseX, int mouseY) {
        graphics.fill(x, y, x + width, y + rowHeight(), Theme.currentTheme.panelAlt());
        graphics.renderOutline(x, y, width, rowHeight(), Theme.currentTheme.border());
        graphics.drawString(this.font, setting.getName(), x + 10, y + 6, Theme.currentTheme.text(), false);
        graphics.drawString(this.font, setting.getDescription(), x + 10, y + 16, Theme.currentTheme.muted(), false);

        if (setting instanceof BooleanSetting booleanSetting) {
            drawToggle(graphics, x + width - 68, y + 8, booleanSetting.getValue());
        } else if (setting instanceof NumberSetting numberSetting) {
            drawSlider(graphics, numberSetting, x + width - 170, y + 10, 150);
        } else if (setting instanceof EnumSetting<?> enumSetting) {
            drawChoicePill(graphics, x + width - 112, y + 8, 94, enumSetting.getValue().name());
        } else if (setting instanceof KeybindSetting keybindSetting) {
            drawChoicePill(graphics, x + width - 112, y + 8, 94, keyName(keybindSetting.getValue()));
        } else if (setting instanceof ColorSetting colorSetting) {
            drawColorSwatch(graphics, x + width - 40, y + 9, colorSetting.getValue());
        }
    }

    private void drawToggle(GuiGraphics graphics, int x, int y, boolean enabled) {
        int width = 48;
        graphics.fill(x, y, x + width, y + 18, enabled ? Theme.currentTheme.accent() : Theme.currentTheme.panelAlt());
        graphics.renderOutline(x, y, width, 18, Theme.currentTheme.border());
        int knobX = enabled ? x + 30 : x + 3;
        graphics.fill(knobX, y + 3, knobX + 15, y + 15, 0xFFFFFFFF);
    }

    private void drawSlider(GuiGraphics graphics, NumberSetting setting, int x, int y, int width) {
        double percent = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int fillWidth = (int) (width * percent);
        graphics.fill(x, y + 10, x + width, y + 14, 0x40000000);
        graphics.fill(x, y + 10, x + fillWidth, y + 14, Theme.currentTheme.accent());
        graphics.fill(x + fillWidth - 2, y + 7, x + fillWidth + 2, y + 17, 0xFFFFFFFF);
        String value = setting.getStep() >= 1.0 ? String.valueOf((int) Math.round(setting.getValue())) : String.format(Locale.ROOT, "%.2f", setting.getValue());
        graphics.drawString(this.font, value, x + width - this.font.width(value), y - 2, Theme.currentTheme.text(), false);
    }

    private void drawChoicePill(GuiGraphics graphics, int x, int y, int width, String value) {
        graphics.fill(x, y, x + width, y + 18, Theme.currentTheme.panel());
        graphics.renderOutline(x, y, width, 18, Theme.currentTheme.border());
        graphics.drawCenteredString(this.font, value, x + width / 2, y + 5, Theme.currentTheme.text());
    }

    private void drawColorSwatch(GuiGraphics graphics, int x, int y, int color) {
        graphics.fill(x, y, x + 18, y + 18, color);
        graphics.renderOutline(x, y, 18, 18, Theme.currentTheme.border());
    }

    private void drawActionButton(GuiGraphics graphics, int x, int y, int width, String label) {
        graphics.fill(x, y, x + width, y + 22, Theme.currentTheme.panelAlt());
        graphics.renderOutline(x, y, width, 22, Theme.currentTheme.border());
        graphics.drawCenteredString(this.font, label, x + width / 2, y + 7, Theme.currentTheme.text());
    }

    private int getExpandedCardHeight(Module module) {
        return 48 + module.getSettings().size() * (rowHeight() + 6) + 6;
    }

    private int rowHeight() {
        return ClientPreferences.LAYOUT_DENSITY.getValue() == ClientPreferences.LayoutDensity.COMPACT ? 34 : 42;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        int sidebarWidth = 176;
        if (mouseX <= sidebarWidth) {
            handleSidebarClick(mouseX, mouseY);
            return true;
        }

        int contentX = sidebarWidth + 20;
        int contentWidth = this.width - contentX - 20;
        int contentY = 20;
        int searchX = contentX + 18;
        int searchY = contentY + 16;
        int searchWidth = contentWidth - 36;
        if (inside(mouseX, mouseY, searchX, searchY, searchWidth, 24)) {
            textFocus = TextFocus.SEARCH;
            listeningModule = null;
            return true;
        }

        textFocus = TextFocus.NONE;
        if (generalPage == null) {
            if (handleModulePageClick(mouseX, mouseY, button, contentX + 18, contentY + 78, contentWidth - 36)) {
                return true;
            }
        } else if (handleGeneralPageClick(mouseX, mouseY, button, contentX + 18, contentY + 78, contentWidth - 36)) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    private void handleSidebarClick(double mouseX, double mouseY) {
        int y = 68;
        for (Category category : Category.values()) {
            if (inside(mouseX, mouseY, 12, y, 152, 22)) {
                selectedCategory = category;
                generalPage = null;
                scrollOffset = 0;
                return;
            }
            y += 28;
        }

        y += 30;
        for (GeneralPage page : GeneralPage.values()) {
            if (inside(mouseX, mouseY, 12, y, 152, 22)) {
                generalPage = page;
                scrollOffset = 0;
                return;
            }
            y += 28;
        }
    }

    private boolean handleModulePageClick(double mouseX, double mouseY, int button, int x, int y, int width) {
        List<Module> modules = ModuleManager.INSTANCE.search(
                selectedCategory,
                searchText,
                ClientPreferences.SEARCH_DESCRIPTIONS.getValue(),
                ClientPreferences.SORT_MODE.getValue() == ClientPreferences.ModuleSortMode.ENABLED_FIRST
        );

        int spacing = ClientPreferences.LAYOUT_DENSITY.getValue() == ClientPreferences.LayoutDensity.COMPACT ? 10 : 16;
        int cursorY = y - (int) scrollOffset;
        for (Module module : modules) {
            int height = expandedModules.contains(module.getName()) ? getExpandedCardHeight(module) : 54;
            if (inside(mouseX, mouseY, x, cursorY, width, height)) {
                if (inside(mouseX, mouseY, x + width - 72, cursorY + 10, 48, 18)) {
                    module.toggle();
                    Config.save();
                    return true;
                }
                if (inside(mouseX, mouseY, x + width - 24, cursorY + 8, 18, 18)) {
                    toggleExpanded(module);
                    return true;
                }
                if (!expandedModules.contains(module.getName())) {
                    toggleExpanded(module);
                    return true;
                }

                int rowY = cursorY + 48;
                for (Setting<?> setting : module.getSettings()) {
                    if (inside(mouseX, mouseY, x + 10, rowY, width - 20, rowHeight())) {
                        return handleSettingClick(module, setting, mouseX, mouseY, button, x + 10, rowY, width - 20);
                    }
                    rowY += rowHeight() + 6;
                }
            }
            cursorY += height + spacing;
        }

        return false;
    }

    private boolean handleGeneralPageClick(double mouseX, double mouseY, int button, int x, int y, int width) {
        if (generalPage == GeneralPage.SETTINGS) {
            int rowY = y - (int) scrollOffset;
            for (Setting<?> setting : ClientPreferences.settings()) {
                if (inside(mouseX, mouseY, x, rowY, width, rowHeight())) {
                    return handleSettingClick(null, setting, mouseX, mouseY, button, x, rowY, width);
                }
                rowY += rowHeight() + 8;
            }
            return false;
        }

        if (generalPage == GeneralPage.THEME) {
            int themeY = y + 16 - (int) scrollOffset;
            int themeX = x;
            for (Theme theme : Theme.values()) {
                if (inside(mouseX, mouseY, themeX, themeY, 120, 48)) {
                    Theme.currentTheme = theme;
                    Config.save();
                    return true;
                }
                themeX += 130;
            }

            int swatchX = x;
            int swatchY = themeY + 86;
            for (AccentPreset accent : AccentPreset.values()) {
                if (inside(mouseX, mouseY, swatchX, swatchY, 22, 22)) {
                    Theme.currentAccent = accent;
                    Config.save();
                    return true;
                }
                swatchX += 32;
            }
            return false;
        }

        if (generalPage == GeneralPage.CONFIGS) {
            int fieldY = y + 14 - (int) scrollOffset;
            if (inside(mouseX, mouseY, x, fieldY, width - 180, 22)) {
                textFocus = TextFocus.CONFIG_NAME;
                return true;
            }
            if (inside(mouseX, mouseY, x + width - 168, fieldY, 48, 22)) {
                Config.saveProfile(configName);
                return true;
            }
            if (inside(mouseX, mouseY, x + width - 114, fieldY, 48, 22)) {
                Config.loadProfile(configName);
                return true;
            }
            if (inside(mouseX, mouseY, x + width - 60, fieldY, 48, 22)) {
                Config.deleteProfile(configName);
                return true;
            }

            int listY = fieldY + 50;
            for (String profile : Config.listProfiles()) {
                if (inside(mouseX, mouseY, x, listY, width, 22)) {
                    configName = profile;
                    return true;
                }
                listY += 28;
            }
            return false;
        }

        if (generalPage == GeneralPage.KEYBINDS) {
            int rowY = y - (int) scrollOffset;
            for (Module module : ModuleManager.INSTANCE.search(null, searchText, true, false)) {
                int bindX = x + width - 98;
                if (inside(mouseX, mouseY, bindX, rowY + 5, 86, 20)) {
                    listeningModule = module;
                    return true;
                }
                rowY += 38;
            }
        }

        return false;
    }

    private boolean handleSettingClick(Module owner, Setting<?> setting, double mouseX, double mouseY, int button, int x, int y, int width) {
        if (setting instanceof BooleanSetting booleanSetting) {
            booleanSetting.setValue(!booleanSetting.getValue());
        } else if (setting instanceof NumberSetting numberSetting) {
            draggingSlider = true;
            draggedSetting = setting;
            updateSlider(numberSetting, mouseX, x + width - 170, 150);
        } else if (setting instanceof EnumSetting<?> enumSetting) {
            if (button == 1) {
                enumSetting.cycleBackward();
            } else {
                enumSetting.cycleForward();
            }
        } else if (setting instanceof KeybindSetting) {
            listeningModule = owner;
        } else if (setting instanceof ColorSetting colorSetting) {
            cycleColor(colorSetting, button == 1 ? -1 : 1);
        }

        Config.save();
        return true;
    }

    private void updateSlider(NumberSetting setting, double mouseX, int sliderX, int width) {
        double percent = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / width));
        double value = setting.getMin() + percent * (setting.getMax() - setting.getMin());
        setting.setValue(value);
    }

    private void cycleColor(ColorSetting setting, int direction) {
        int current = setting.getValue();
        int index = 0;
        for (int i = 0; i < COLOR_SWATCHES.length; i++) {
            if (COLOR_SWATCHES[i] == current) {
                index = i;
                break;
            }
        }
        int next = (index + direction + COLOR_SWATCHES.length) % COLOR_SWATCHES.length;
        setting.setValue(COLOR_SWATCHES[next]);
    }

    private void toggleExpanded(Module module) {
        if (!expandedModules.add(module.getName())) {
            expandedModules.remove(module.getName());
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingSlider = false;
        draggedSetting = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (draggingSlider && draggedSetting instanceof NumberSetting numberSetting) {
            int sidebarWidth = 176;
            int contentX = sidebarWidth + 20;
            int contentWidth = this.width - contentX - 20;
            updateSlider(numberSetting, event.x(), contentX + contentWidth - 188, 150);
            Config.save();
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scrollOffset = Math.max(0, scrollOffset - vertical * 18);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listeningModule != null) {
            if (event.key() == GLFW.GLFW_KEY_BACKSPACE || event.key() == GLFW.GLFW_KEY_DELETE) {
                listeningModule.setKeybind(0);
            } else if (event.key() != GLFW.GLFW_KEY_ESCAPE) {
                listeningModule.setKeybind(event.key());
            }
            listeningModule = null;
            Config.save();
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ESCAPE || event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.onClose();
            return true;
        }

        if (textFocus == TextFocus.SEARCH && event.key() == GLFW.GLFW_KEY_BACKSPACE && !searchText.isEmpty()) {
            searchText = searchText.substring(0, searchText.length() - 1);
            return true;
        }

        if (textFocus == TextFocus.CONFIG_NAME && event.key() == GLFW.GLFW_KEY_BACKSPACE && !configName.isEmpty()) {
            configName = configName.substring(0, configName.length() - 1);
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!event.isAllowedChatCharacter()) {
            return super.charTyped(event);
        }

        String typed = event.codepointAsString();
        if (textFocus == TextFocus.SEARCH) {
            searchText += typed;
            return true;
        }
        if (textFocus == TextFocus.CONFIG_NAME) {
            configName += typed;
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public void onClose() {
        Config.save();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    private String keyName(int key) {
        if (key == 0) {
            return "None";
        }
        String glfwName = GLFW.glfwGetKeyName(key, 0);
        if (glfwName != null && !glfwName.isBlank()) {
            return glfwName.toUpperCase(Locale.ROOT);
        }

        return switch (key) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_ESCAPE -> "ESC";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            default -> "KEY " + key;
        };
    }
}
