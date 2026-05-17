package com.falaris.client.ui.clickgui;

import com.falaris.client.config.Config;
import com.falaris.client.modules.*;
import com.falaris.client.modules.setting.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class ClickGUI extends Screen {
    private enum GeneralPage { SETTINGS, THEME, CONFIGS, KEYBINDS }
    private static final int[] COLORS = { 0xFFFFA31A, 0xFFF43F5E, 0xFF06B6D4, 0xFF10B981, 0xFF8B5CF6 };
    
    private Category selectedCategory = Category.COMBAT;
    private GeneralPage generalPage = null;
    private final Set<String> expandedModules = new HashSet<>();
    private int guiWidth = 700, guiHeight = 500;
    private double contentScroll = 0.0;
    private int lastContentHeight = 0;
    private String searchQuery = "";
    private boolean searchFocused = false;
    private com.falaris.client.modules.Module popupModule = null;
    private int popupX = 0, popupY = 0;
    private boolean awaitingBind = false;
    private com.falaris.client.modules.Module bindingModule = null;
    private EditBox searchBox = null;

    public ClickGUI() { super(Component.literal("Falaris")); }

    @Override
    protected void init() {
        super.init();
        int gx = (this.width - guiWidth) / 2, gy = (this.height - guiHeight) / 2;
        int sx = gx + 170;
        int sy = gy + 20;
        searchBox = new EditBox(this.font, sx, sy, Math.max(100, guiWidth - 190), 18, Component.literal("Search"));
        searchBox.setValue(searchQuery);
        this.addRenderableWidget(searchBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Compute a responsive GUI size so it matches the current GUI scale
        guiWidth = Math.min(700, Math.max(200, this.width - 40));
        guiHeight = Math.min(500, Math.max(120, this.height - 40));

        int gx = (this.width - guiWidth) / 2, gy = (this.height - guiHeight) / 2;
        // Background and panels use theme colors for a softer look (with soft shadow)
        drawPanel(graphics, gx, gy, guiWidth, guiHeight, Theme.currentTheme.background(), Theme.currentTheme.border());
        drawPanel(graphics, gx, gy, 150, guiHeight, Theme.currentTheme.sidebar(), Theme.currentTheme.border());
        drawSidebar(graphics, gx, gy, mouseX, mouseY);
        drawContent(graphics, gx + 170, gy + 20, guiWidth - 190, guiHeight - 40, mouseX, mouseY);
    }

    private void drawSidebar(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        int iy = y + 20;
        graphics.drawString(this.font, "Falaris", x + 10, iy - 12, Theme.currentTheme.text());
        iy += 8;
        for (Category c : Category.values()) {
            int color = selectedCategory == c && generalPage == null ? Theme.currentTheme.accent() : Theme.currentTheme.muted();
            // hover highlight for categories
            if (mouseX >= x && mouseX <= x + 150 && mouseY >= iy && mouseY <= iy + 20) color = Theme.currentTheme.text();
            graphics.drawString(this.font, c.getDisplayName(), x + 10, iy, color);
            iy += 30;
        }
        iy += 20;
        for (GeneralPage p : GeneralPage.values()) {
            int color = generalPage == p ? Theme.currentTheme.accent() : Theme.currentTheme.muted();
            if (mouseX >= x && mouseX <= x + 150 && mouseY >= iy && mouseY <= iy + 20) color = Theme.currentTheme.text();
            graphics.drawString(this.font, p.name(), x + 10, iy, color);
            iy += 30;
        }
    }

    private void drawContent(GuiGraphics graphics, int x, int y, int w, int h, int mouseX, int mouseY) {
        if (generalPage == GeneralPage.THEME) {
            graphics.drawString(this.font, "Theme Selection", x, y, -1);
            for (int i = 0; i < COLORS.length; i++) graphics.fill(x + i * 30, y + 20, x + i * 30 + 20, y + 40, COLORS[i]);
            graphics.drawString(this.font, "Accent: " + Theme.currentAccent.name(), x, y + 45, -1);
        } else if (generalPage == GeneralPage.SETTINGS) {
            graphics.drawString(this.font, "Client Settings (WIP)", x, y, -1);
        } else if (generalPage == GeneralPage.KEYBINDS) {
            int iy = y;
            for (com.falaris.client.modules.Module m : ModuleManager.INSTANCE.getModules()) {
                graphics.drawString(this.font, m.getName() + " [" + GLFW.glfwGetKeyName(m.getKeybind(), 0) + "]", x, iy, -1);
                iy += 20;
            }
        } else if (generalPage == GeneralPage.CONFIGS) {
            graphics.drawString(this.font, "Active Config: " + Config.getActiveProfile(), x, y, -1);
        } else if (generalPage == null) {
            // Header accent and search box (soft panel)
            graphics.fill(x, y, x + w, y + 4, Theme.currentTheme.accent());
            drawPanel(graphics, x, y + 4, w, 22, 0xFF222222, 0xFF2B2B2B);
            graphics.drawString(this.font, "Search: " + (searchQuery.isEmpty() ? "(type to search)" : searchQuery), x + 8, y + 9, Theme.currentTheme.muted());

            // sync searchQuery from the EditBox if present
            if (searchBox != null) searchQuery = searchBox.getValue();
            int iy = y + 26 - (int) contentScroll;
            int total = 0;
            for (com.falaris.client.modules.Module m : ModuleManager.INSTANCE.getModulesByCategory(selectedCategory)) {
                if (!m.matchesSearch(searchQuery, false)) continue;
                // Only draw visible rows
                    if (iy + 25 >= y + 26 && iy <= y + h) {
                        // hover effect
                        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= iy && mouseY <= iy + 25;
                        int rowBg = hovered ? Theme.currentTheme.panelAlt() : 0xFF333333;
                        int rowBorder = hovered ? Theme.currentTheme.border() : 0xFF3A3A3A;
                        drawPanel(graphics, x, iy, w, 25, rowBg, rowBorder);
                        // bubbly accent: draw a small square accent at the left
                        graphics.fill(x + 6, iy + 6, x + 16, iy + 16, m.isEnabled() ? Theme.currentTheme.accent() : 0xFF555555);
                        graphics.drawString(this.font, m.getName(), x + 22, iy + 8, m.isEnabled() ? Theme.currentTheme.accent() : Theme.currentTheme.text());
                    }
                iy += 30;
                total += 30;
            }
            lastContentHeight = Math.max(0, total - (h - 26));

            // render popup if present
                if (popupModule != null) {
                int pw = 180, ph = 80;
                int px = Math.max(x, Math.min(x + w - pw, popupX));
                int py = Math.max(y + 26, Math.min(y + h - ph, popupY));
                drawPanel(graphics, px, py, pw, ph, 0xFF111111, 0xFF333333);
                graphics.drawString(this.font, popupModule.getName(), px + 8, py + 6, Theme.currentTheme.text());
                String keyName = GLFW.glfwGetKeyName(popupModule.getKeybind(), 0);
                graphics.drawString(this.font, "Keybind: " + (keyName == null ? "None" : keyName), px + 8, py + 22, Theme.currentTheme.muted());
                graphics.drawString(this.font, awaitingBind && bindingModule == popupModule ? "Press a key..." : "[Set Keybind]", px + 8, py + 40, 0xFFFFA31A);
            }

            // If awaiting a bind, poll keyboard state to capture a key press
            if (awaitingBind && bindingModule != null) {
                long handle = Minecraft.getInstance().getWindow().handle();
                for (int k = 32; k <= 348; k++) {
                    if (GLFW.glfwGetKey(handle, k) == GLFW.GLFW_PRESS) {
                        bindingModule.setKeybind(k);
                        awaitingBind = false;
                        bindingModule = null;
                        popupModule = null;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean consumed) {
        double mx = event.x();
        double my = event.y();
        int b = event.button();

        int gx = (this.width - guiWidth) / 2, gy = (this.height - guiHeight) / 2;
        // Sidebar clicks (category / general pages)
        if (mx >= gx && mx <= gx + 150) {
            int iy = gy + 20;
            for (Category c : Category.values()) {
                if (my >= iy && my <= iy + 20) { selectedCategory = c; generalPage = null; return true; }
                iy += 30;
            }
            iy += 20;
            for (GeneralPage p : GeneralPage.values()) {
                if (my >= iy && my <= iy + 20) { generalPage = p; return true; }
                iy += 30;
            }
        }

        // Content area clicks (modules list)
        int contentX = gx + 170;
        int contentY = gy + 20;
        int contentW = guiWidth - 190;
        int contentH = guiHeight - 40;
        if (mx >= contentX && mx <= contentX + contentW && my >= contentY && my <= contentY + contentH) {
            if (generalPage == null) {
                int iy = contentY + 26 - (int) contentScroll;
                for (com.falaris.client.modules.Module m : ModuleManager.INSTANCE.getModulesByCategory(selectedCategory)) {
                    if (!m.matchesSearch(searchQuery, false)) { continue; }
                    // Each module row uses 25px height with 30px spacing
                    if (my >= iy && my <= iy + 25) {
                        if (b == GLFW.GLFW_MOUSE_BUTTON_1) {
                            m.toggle();
                            popupModule = null;
                            return true;
                        } else if (b == GLFW.GLFW_MOUSE_BUTTON_2) {
                            // Right click: open inline popup instead of full screen
                            popupModule = m;
                            popupX = (int) mx;
                            popupY = (int) my;
                            // If clicked on popup keybind area immediately
                            return true;
                        }
                    }
                    iy += 30;
                }
                // clicking content area closes popup if clicked outside
                popupModule = null;
            }
            else if (generalPage == GeneralPage.THEME) {
                // handle theme accent selection
                for (int i = 0; i < COLORS.length; i++) {
                    int bx = contentX + (i * 30);
                    int by = contentY;
                    if (mx >= bx && mx <= bx + 20 && my >= by + 20 && my <= by + 40) {
                        if (b == GLFW.GLFW_MOUSE_BUTTON_1) {
                            // left click: set accent
                            Theme.currentAccent = AccentPreset.values()[i % AccentPreset.values().length];
                            return true;
                        } else if (b == GLFW.GLFW_MOUSE_BUTTON_2) {
                            // right click: cycle theme
                            Theme[] t = Theme.values();
                            int next = (Theme.currentTheme.ordinal() + 1) % t.length;
                            Theme.currentTheme = t[next];
                            return true;
                        }
                    }
                }
            }
            // Future content interactions (theme pick, etc.) go here
            return true;
        }

        // If popup is open, check clicks on popup buttons (set keybind)
        if (popupModule != null) {
            int pw = 180, ph = 80;
            int px = Math.max(contentX, Math.min(contentX + (guiWidth - 190) - pw, popupX));
            int py = Math.max(contentY, Math.min(contentY + (guiHeight - 40) - ph, popupY));
            if (mx >= px + 8 && mx <= px + 8 + 120 && my >= py + 40 && my <= py + 40 + 16) {
                // clicked on [Set Keybind]
                awaitingBind = true;
                bindingModule = popupModule;
                return true;
            }
        }

        return super.mouseClicked(event, consumed);
    }

    // Key input handling is done via polling when awaiting a bind to avoid mapping differences.

    private void drawPanel(GuiGraphics graphics, int x, int y, int w, int h, int bg, int border) {
        // soft drop shadow
        graphics.fill(x + 4, y + 4, x + w + 4, y + h + 4, 0x44000000);
        // main panel
        graphics.fill(x, y, x + w, y + h, bg);
        // outline
        graphics.renderOutline(x, y, w, h, border);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double unused) {
        // Only scroll content if the mouse is over the content area
        int gx = (this.width - guiWidth) / 2, gy = (this.height - guiHeight) / 2;
        int contentX = gx + 170;
        int contentY = gy + 20;
        int contentW = guiWidth - 190;
        int contentH = guiHeight - 40;
        if (mouseX >= contentX && mouseX <= contentX + contentW && mouseY >= contentY && mouseY <= contentY + contentH) {
            contentScroll -= amount * 20.0;
            if (contentScroll < 0) contentScroll = 0;
            if (contentScroll > lastContentHeight) contentScroll = lastContentHeight;
            return true;
        }
        return true;
    }

    // Keyboard and character input handled by EditBox widget.
}
