package com.falaris.client.modules;

import com.falaris.client.modules.combat.AutoTotem;
import com.falaris.client.modules.combat.KillAura;
import com.falaris.client.modules.combat.SilentAura;
import com.falaris.client.modules.combat.TriggerBot;
import com.falaris.client.modules.mace.AutoAttributeSwap;
import com.falaris.client.modules.misc.Timer;
import com.falaris.client.modules.movement.Sprint;
import com.falaris.client.modules.render.ESP;
import com.falaris.client.modules.render.FullBright;
import com.falaris.client.modules.render.Tracers;
import net.minecraft.client.Camera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> moduleMap = new HashMap<>();
    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    private ModuleManager() {
        addModule(new TriggerBot());
        addModule(new SilentAura());
        addModule(new KillAura());
        addModule(new AutoTotem());
        addModule(new AutoAttributeSwap());
        addModule(new ESP());
        addModule(new Tracers());
        addModule(new FullBright());
        addModule(new Sprint());
        addModule(new Timer());
    }

    private void addModule(Module module) {
        modules.add(module);
        moduleMap.put(module.getClass(), module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(module -> module.getCategory() == category)
                .sorted(Comparator.comparing(Module::getName))
                .toList();
    }

    public List<Module> search(Category category, String query, boolean includeDescription, boolean enabledFirst) {
        Comparator<Module> comparator = Comparator.comparing(Module::getName);
        if (enabledFirst) {
            comparator = Comparator.comparing(Module::isEnabled).reversed().thenComparing(Module::getName);
        }

        return modules.stream()
                .filter(module -> category == null || module.getCategory() == category)
                .filter(module -> module.matchesSearch(query, includeDescription))
                .sorted(comparator)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) moduleMap.get(clazz);
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    public void onRender2D() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender2D();
            }
        }
    }

    public void onRender3D(Camera camera, float tickDelta) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender3D(camera, tickDelta);
            }
        }
    }

    public void handleKeybinds() {
        for (Module module : modules) {
            int key = module.getKeybind();
            if (key <= 0) {
                continue;
            }

            boolean isPressed = org.lwjgl.glfw.GLFW.glfwGetKey(module.mc.getWindow().handle(), key) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean wasPressed = pressedKeys.getOrDefault(key, false);
            if (isPressed && !wasPressed) {
                module.toggle();
            }
            pressedKeys.put(key, isPressed);
        }
    }
}
