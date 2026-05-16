package com.falaris.client.ui.clickgui;

public enum AccentPreset {
    AMBER(0xFFF59E0B),
    ROSE(0xFFF43F5E),
    CYAN(0xFF06B6D4),
    EMERALD(0xFF10B981),
    VIOLET(0xFF8B5CF6),
    CRIMSON(0xFFEF4444);

    private final int color;

    AccentPreset(int color) {
        this.color = color;
    }

    public int color() {
        return color;
    }
}
