package com.falaris.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, String description, int defaultValue) {
        super(name, description, defaultValue);
    }

    public float red() {
        return ((getValue() >> 16) & 0xFF) / 255.0f;
    }

    public float green() {
        return ((getValue() >> 8) & 0xFF) / 255.0f;
    }

    public float blue() {
        return (getValue() & 0xFF) / 255.0f;
    }

    public float alpha() {
        return ((getValue() >> 24) & 0xFF) / 255.0f;
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
