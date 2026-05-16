package com.falaris.client.modules;

public enum Category {
    COMBAT("Combat"),
    MACE("Mace"),
    SPEAR("Spear"),
    VISUAL("Visual"),
    MOVEMENT("Movement"),
    MISC("Misc");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
