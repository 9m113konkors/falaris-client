package com.falaris.client.modules;

import com.falaris.client.modules.combat.*;
import com.falaris.client.modules.mace.*;
import com.falaris.client.modules.misc.*;
import com.falaris.client.modules.movement.*;
import com.falaris.client.modules.render.*;
import com.falaris.client.modules.render.ModuleList;
import com.falaris.client.modules.misc.TimerModule;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> moduleMap = new HashMap<>();
    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();

    private ModuleManager() {
        // Combat
        addModule(new TriggerBot());
        addModule(new SilentAura());
        addModule(new KillAura());
        addModule(new AutoTotem());
        addModule(new Criticals());
        addModule(new CrystalAura());
        addModule(new BowAimbot());
        addModule(new AntiKnockback());
        addModule(new Velocity());
        addModule(new AimAssist());
        // Movement
        addModule(new Sprint());
        addModule(new Flight());
        addModule(new NoFall());
        addModule(new Speed());
        addModule(new ElytraFly());
        // Mace
        addModule(new AutoAttributeSwap());
        addModule(new MaceDMG());
        // Render
        addModule(new ESP());
        addModule(new Tracers());
        addModule(new FullBright());
        addModule(new NameTags());
        addModule(new XRay());
        addModule(new Search());
        addModule(new ModuleList());
        // Misc
        addModule(new AutoWeapon());
        addModule(new AutoArmor());
        addModule(new VillagerRoller());
        addModule(new ClientCosmetics());
        addModule(new TimerModule());
        addModule(new AutoSteal());
        addModule(new Surround());
        addModule(new AutoEat());
        addModule(new AntiHunger());
        addModule(new AutoWeb());
        addModule(new FastPlace());
        addModule(new AutoReconnect());
        addModule(new SpearKill());
        addModule(new NameProtect());
    }

    private void addModule(Module module) {
        modules.add(module);
        moduleMap.put(module.getClass(), module);
    }

    public List<Module> getModules() { return modules; }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).sorted(Comparator.comparing(Module::getName)).toList();
    }

    public <T extends Module> T getModule(Class<T> clazz) { return (T) moduleMap.get(clazz); }

    public void onTick() { modules.stream().filter(Module::isEnabled).forEach(Module::onTick); }

    public void onRender2D(GuiGraphics graphics) {
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onRender2D(graphics);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void onRender3D(Camera camera, float delta) {
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onRender3D(camera, delta);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void handleKeybinds() {
        for (Module m : modules) {
            int key = m.getKeybind();
            if (key <= 0) continue;
            boolean isPressed = org.lwjgl.glfw.GLFW.glfwGetKey(m.mc.getWindow().handle(), key) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            boolean wasPressed = pressedKeys.getOrDefault(key, false);
            if (isPressed && !wasPressed) m.toggle();
            pressedKeys.put(key, isPressed);
        }
    }
}
