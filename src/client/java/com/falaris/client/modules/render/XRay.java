package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

public class XRay extends Module {
    public final BooleanSetting showOres    = addSetting(new BooleanSetting("Show Ores",   "Show ore blocks through walls", true));
    public final BooleanSetting showChests  = addSetting(new BooleanSetting("Show Chests", "Show chests & barrels",         true));

    public static XRay instance;
    public static Set<Block> xrayBlocks = new HashSet<>();

    public XRay() {
        super("XRay", "See ore blocks through terrain", Category.VISUAL, "xray");
        if (instance == null) instance = this;
        rebuildBlockSet();
    }

    @Override
    public void onEnable() { rebuildBlockSet(); }
    @Override
    public void onDisable() { xrayBlocks.clear(); }

    private void rebuildBlockSet() {
        xrayBlocks.clear();
        String[] blockIds = {
            "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore",
            "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore",
            "minecraft:ancient_debris", "minecraft:chest", "minecraft:barrel",
            "minecraft:trapped_chest","minecraft:nether_gold_ore",
            "minecraft:gold_ore","minecraft:deepslate_gold_ore",
            "minecraft:iron_ore","minecraft:deepslate_iron_ore",
            "minecraft:copper_ore","minecraft:deepslate_copper_ore",
            "minecraft:lapis_ore","minecraft:deepslate_lapis_ore",
            "minecraft:redstone_ore","minecraft:deepslate_redstone_ore",
            "minecraft:coal_ore","minecraft:deepslate_coal_ore",
        };
        if (mc.level != null) {
            var reg = mc.level.registryAccess().lookup(net.minecraft.core.registries.Registries.BLOCK);
            for (String id : blockIds) {
                try {
                    Optional<Block> b = reg.get(net.minecraft.resources.ResourceLocation.parse(id));
                    b.ifPresent(xrayBlocks::add);
                } catch (Exception ignored) {}
            }
        }
    }

    public boolean shouldRenderBlock(Block block) {
        return xrayBlocks.contains(block);
    }
}
