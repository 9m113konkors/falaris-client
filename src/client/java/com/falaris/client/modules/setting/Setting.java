package com.falaris.client.modules.setting;

import com.google.gson.JsonElement;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    private final T defaultValue;
    private T value;

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = sanitize(value);
    }

    protected T sanitize(T value) {
        return value;
    }

    public void reset() {
        value = defaultValue;
    }

    public abstract JsonElement serialize();

    public abstract void deserialize(JsonElement element);
}
