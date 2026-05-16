package com.falaris.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class KeybindSetting extends Setting<Integer> {
    public KeybindSetting(String name, String description, int defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    protected Integer sanitize(Integer value) {
        return Math.max(0, value);
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void deserialize(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsInt());
        }
    }
}
