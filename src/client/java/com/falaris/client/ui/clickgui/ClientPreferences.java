package com.falaris.client.ui.clickgui;

import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.EnumSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.modules.setting.Setting;

import java.util.List;

public final class ClientPreferences {
    public enum LayoutDensity {
        COMPACT,
        COMFORTABLE
    }

    public enum ModuleSortMode {
        ALPHABETICAL,
        ENABLED_FIRST
    }

    public static final NumberSetting GUI_SCALE = new NumberSetting("GUI Scale", "Overall UI scale", 1.0, 0.85, 1.3, 0.05);
    public static final EnumSetting<LayoutDensity> LAYOUT_DENSITY = new EnumSetting<>("Layout Density", "Spacing preset for the GUI", LayoutDensity.COMFORTABLE, LayoutDensity.values());
    public static final BooleanSetting ANIMATIONS = new BooleanSetting("Animations", "Use lightweight GUI motion", true);
    public static final BooleanSetting SEARCH_DESCRIPTIONS = new BooleanSetting("Search Descriptions", "Include descriptions in search results", true);
    public static final EnumSetting<ModuleSortMode> SORT_MODE = new EnumSetting<>("Sort Mode", "How modules are sorted", ModuleSortMode.ALPHABETICAL, ModuleSortMode.values());
    public static final BooleanSetting SHOW_HUD = new BooleanSetting("Show HUD", "Render the in-game HUD", true);
    public static final BooleanSetting SHOW_COORDS = new BooleanSetting("Show Coordinates", "Show XYZ coordinates on the HUD", true);
    public static final BooleanSetting SHOW_SERVER = new BooleanSetting("Show Server", "Show server info on the HUD", true);
    public static final BooleanSetting SHOW_WATERMARK = new BooleanSetting("Show Watermark", "Show the Falaris watermark", true);

    private ClientPreferences() {}

    public static List<Setting<?>> settings() {
        return List.of(
                GUI_SCALE,
                LAYOUT_DENSITY,
                ANIMATIONS,
                SEARCH_DESCRIPTIONS,
                SORT_MODE,
                SHOW_HUD,
                SHOW_COORDS,
                SHOW_SERVER,
                SHOW_WATERMARK
        );
    }
}
