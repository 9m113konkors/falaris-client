package com.falaris.client.config;

import com.falaris.client.modules.Module;
import com.falaris.client.modules.ModuleManager;
import com.falaris.client.modules.setting.Setting;
import com.falaris.client.ui.clickgui.AccentPreset;
import com.falaris.client.ui.clickgui.ClientPreferences;
import com.falaris.client.ui.clickgui.Theme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(Minecraft.getInstance().gameDirectory, "falaris");
    private static final File PROFILE_DIR = new File(CONFIG_DIR, "configs");
    private static String activeProfile = "default";

    private Config() {}

    public static void load() {
        loadProfile(activeProfile);
    }

    public static void save() {
        saveProfile(activeProfile);
    }

    public static String getActiveProfile() {
        return activeProfile;
    }

    public static void setActiveProfile(String profileName) {
        activeProfile = sanitizeProfileName(profileName);
    }

    public static List<String> listProfiles() {
        if (!PROFILE_DIR.exists()) {
            return List.of();
        }

        String[] fileNames = PROFILE_DIR.list((dir, name) -> name.endsWith(".json"));
        if (fileNames == null) {
            return List.of();
        }

        Arrays.sort(fileNames, String::compareToIgnoreCase);
        List<String> profiles = new ArrayList<>();
        for (String fileName : fileNames) {
            profiles.add(fileName.substring(0, fileName.length() - 5));
        }
        return profiles;
    }

    public static void saveProfile(String profileName) {
        try {
            ensureDirectories();
            String safeName = sanitizeProfileName(profileName);
            File file = new File(PROFILE_DIR, safeName + ".json");
            JsonObject root = new JsonObject();
            root.addProperty("profile", safeName);
            root.add("preferences", serializePreferences());
            root.add("modules", serializeModules());

            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(root, writer);
            }

            activeProfile = safeName;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void loadProfile(String profileName) {
        try {
            ensureDirectories();
            String safeName = sanitizeProfileName(profileName);
            File file = new File(PROFILE_DIR, safeName + ".json");
            if (!file.exists()) {
                return;
            }

            try (FileReader reader = new FileReader(file)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                if (root == null) {
                    return;
                }

                if (root.has("preferences")) {
                    deserializePreferences(root.getAsJsonObject("preferences"));
                }

                if (root.has("modules")) {
                    deserializeModules(root.getAsJsonObject("modules"));
                }

                activeProfile = safeName;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void deleteProfile(String profileName) {
        File file = new File(PROFILE_DIR, sanitizeProfileName(profileName) + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    private static void ensureDirectories() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
        if (!PROFILE_DIR.exists()) {
            PROFILE_DIR.mkdirs();
        }
    }

    private static JsonObject serializePreferences() {
        JsonObject json = new JsonObject();
        json.addProperty("theme", Theme.currentTheme.name());
        json.addProperty("accent", Theme.currentAccent.name());

        for (Setting<?> setting : ClientPreferences.settings()) {
            json.add(setting.getName(), setting.serialize());
        }

        return json;
    }

    private static void deserializePreferences(JsonObject json) {
        if (json.has("theme")) {
            Theme.currentTheme = Theme.valueOf(json.get("theme").getAsString());
        }
        if (json.has("accent")) {
            Theme.currentAccent = AccentPreset.valueOf(json.get("accent").getAsString());
        }

        for (Setting<?> setting : ClientPreferences.settings()) {
            if (json.has(setting.getName())) {
                setting.deserialize(json.get(setting.getName()));
            }
        }
    }

    private static JsonObject serializeModules() {
        JsonObject json = new JsonObject();
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("enabled", module.isEnabled());

            JsonObject settingsJson = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                settingsJson.add(setting.getName(), setting.serialize());
            }

            moduleJson.add("settings", settingsJson);
            json.add(module.getName(), moduleJson);
        }
        return json;
    }

    private static void deserializeModules(JsonObject json) {
        for (Module module : ModuleManager.INSTANCE.getModules()) {
            if (!json.has(module.getName())) {
                continue;
            }

            JsonObject moduleJson = json.getAsJsonObject(module.getName());
            if (moduleJson.has("settings")) {
                JsonObject settingsJson = moduleJson.getAsJsonObject("settings");
                for (Setting<?> setting : module.getSettings()) {
                    JsonElement settingElement = settingsJson.get(setting.getName());
                    if (settingElement != null) {
                        setting.deserialize(settingElement);
                    }
                }
            }

            if (moduleJson.has("enabled")) {
                module.setEnabled(moduleJson.get("enabled").getAsBoolean());
            }
        }
    }

    private static String sanitizeProfileName(String profileName) {
        if (profileName == null || profileName.isBlank()) {
            return "default";
        }

        return profileName.replaceAll("[^a-zA-Z0-9-_ ]", "").trim().replace(' ', '_');
    }
}
