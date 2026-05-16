package com.falaris.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class EnumSetting<E extends Enum<E>> extends Setting<E> {
    private final E[] values;

    public EnumSetting(String name, String description, E defaultValue, E[] values) {
        super(name, description, defaultValue);
        this.values = values;
    }

    public E[] getValues() {
        return values;
    }

    public void cycleForward() {
        int index = indexOf(getValue());
        setValue(values[(index + 1) % values.length]);
    }

    public void cycleBackward() {
        int index = indexOf(getValue());
        setValue(values[(index - 1 + values.length) % values.length]);
    }

    private int indexOf(E value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(getValue().name());
    }

    @Override
    public void deserialize(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }

        String name = element.getAsString();
        for (E value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                setValue(value);
                return;
            }
        }
    }
}
