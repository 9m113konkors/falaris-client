package com.falaris.client.modules;

import com.falaris.client.modules.setting.KeybindSetting;
import com.falaris.client.modules.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class Module {
    protected final Minecraft mc = Minecraft.getInstance();
    private final String name;
    private final String description;
    private final Category category;
    private final List<String> aliases = new ArrayList<>();
    private final List<Setting<?>> settings = new ArrayList<>();
    private final KeybindSetting keybind;
    private boolean enabled;

    protected Module(String name, String description, Category category, int defaultKey, String... aliases) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = addSetting(new KeybindSetting("Bind", "Keybind for " + name, defaultKey));
        this.aliases.add(name.toLowerCase(Locale.ROOT));
        Arrays.stream(aliases)
                .map(alias -> alias.toLowerCase(Locale.ROOT))
                .forEach(this.aliases::add);
    }

    protected Module(String name, String description, Category category, String... aliases) {
        this(name, description, category, 0, aliases);
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public List<Setting<?>> getSettings() { return settings; }
    public int getKeybind() { return keybind.getValue(); }
    public void setKeybind(int key) { keybind.setValue(key); }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    public boolean matchesSearch(String query, boolean includeDescription) {
        if (query == null || query.isBlank()) return true;
        String lowered = query.toLowerCase(Locale.ROOT);
        if (name.toLowerCase(Locale.ROOT).contains(lowered)) return true;
        if (includeDescription && description.toLowerCase(Locale.ROOT).contains(lowered)) return true;
        return aliases.stream().anyMatch(alias -> alias.contains(lowered));
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onRender2D(GuiGraphics graphics) {}
    public void onRender3D(Camera camera, float tickDelta) {}
}
