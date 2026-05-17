package com.falaris.client.ui.clickgui;

public enum Theme {
    GRAPHITE(0xFF161311, 0xFF1D1815, 0xFF241E19, 0xFF2D261F, 0xFF4A3B2B, 0xFFF5E7D3, 0xFFBDAF9C),
    OBSIDIAN(0xFF0F1014, 0xFF151821, 0xFF1B1F2A, 0xFF232938, 0xFF394155, 0xFFF3F5F7, 0xFFA0A8B8),
    FOREST(0xFF101511, 0xFF162019, 0xFF1D2820, 0xFF233027, 0xFF395042, 0xFFE6F1E8, 0xFF95B09A),
    DUSK(0xFF151018, 0xFF1E1622, 0xFF251D2C, 0xFF30253A, 0xFF493A56, 0xFFF4ECFA, 0xFFB9A8C4),
    CATPPUCCIN(0xFF1E1B1D, 0xFF2A2629, 0xFF332F31, 0xFF3C3638, 0xFF7C6F83, 0xFFF5E0DC, 0xFFB7B0B8);

    // Default to Catppuccin-style theme
    public static Theme currentTheme = CATPPUCCIN;
    public static AccentPreset currentAccent = AccentPreset.AMBER;

    private final int background;
    private final int sidebar;
    private final int panel;
    private final int panelAlt;
    private final int border;
    private final int text;
    private final int muted;

    Theme(int background, int sidebar, int panel, int panelAlt, int border, int text, int muted) {
        this.background = background;
        this.sidebar = sidebar;
        this.panel = panel;
        this.panelAlt = panelAlt;
        this.border = border;
        this.text = text;
        this.muted = muted;
    }

    public int background() {
        return background;
    }

    public int sidebar() {
        return sidebar;
    }

    public int panel() {
        return panel;
    }

    public int panelAlt() {
        return panelAlt;
    }

    public int border() {
        return border;
    }

    public int text() {
        return text;
    }

    public int muted() {
        return muted;
    }

    public int accent() {
        return currentAccent.color();
    }
}
