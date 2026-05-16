package com.falaris.client.modules.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        setValue(defaultValue);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    @Override
    protected Double sanitize(Double value) {
        double clamped = Math.max(min, Math.min(max, value));
        if (step <= 0) {
            return clamped;
        }

        double rounded = Math.round(clamped / step) * step;
        return Math.max(min, Math.min(max, rounded));
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void deserialize(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            setValue(element.getAsDouble());
        }
    }
}
