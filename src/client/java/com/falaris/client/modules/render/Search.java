package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.NumberSetting;
import com.falaris.client.utils.Render3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.*;

public class Search extends Module {
    public final BooleanSetting showOutline = addSetting(new BooleanSetting("Show Outline", "Draws box around matching blocks", true));
    public final NumberSetting range = addSetting(new NumberSetting("Range", "Maximum scan distance", 32.0, 8.0, 64.0, 1.0));
    public final BooleanSetting showOnlyNearby = addSetting(new BooleanSetting("Nearby Only", "Limit scan to render distance", false));

    public static List<Block> searchBlocks = new ArrayList<>();
    private Set<BlockPos> lastHighlighted = new HashSet<>();

    public Search() {
        super("Search", "Highlights specified blocks in the world", Category.VISUAL, "search");
        if (searchBlocks.isEmpty()) addDefaultBlocks();
    }

    private void addDefaultBlocks() {
        String[] defaults = {
            "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore",
            "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore",
            "minecraft:ancient_debris", "minecraft:chest",
            "minecraft:nether_gold_ore", "minecraft:gold_ore", "minecraft:deepslate_gold_ore",
            "minecraft:lapis_ore", "minecraft:deepslate_lapis_ore",
            "minecraft:redstone_ore", "minecraft:deepslate_redstone_ore",
            "minecraft:iron_ore", "minecraft:deepslate_iron_ore",
            "minecraft:coal_ore", "minecraft:deepslate_coal_ore",
        };
        for (String id : defaults) addBlockById(id);
    }

    @Override
    public void onRender3D(Camera camera, float tickDelta) {
        if (mc.level == null || mc.player == null || searchBlocks.isEmpty()) return;
        Vec3 camPos = camera.position();
        double maxDist = range.getValue();

        Set<BlockPos> currentMatch = new HashSet<>();
        int scan = (int) Math.ceil(maxDist);

        BlockPos center = mc.player.blockPosition();
        for (int bx = center.getX() - scan; bx <= center.getX() + scan; bx += 4) {
            for (int by = center.getY() - scan; by <= center.getY() + scan; by += 4) {
                for (int bz = center.getZ() - scan; bz <= center.getZ() + scan; bz += 4) {
                    if (camPos.distanceToSqr(bx + 0.5, by + 0.5, bz + 0.5) > maxDist * maxDist) continue;
                    if (showOnlyNearby.getValue() && !mc.level.hasChunkAt(bx >> 4, bz >> 4)) continue;

                    BlockPos bp = new BlockPos(bx, by, bz);
                    BlockState state = mc.level.getBlockState(bp);
                    if (searchBlocks.contains(state.getBlock())) {
                        currentMatch.add(bp);
                    }
                }
            }
        }
        lastHighlighted = currentMatch;

        PoseStack poseStack = new PoseStack();
        for (BlockPos bp : currentMatch) {
            Render3D.drawBox(poseStack, new AABB(bp), 1.0f, 0.75f, 0.1f, 0.25f, 1.5f);
        }
    }

    public void addBlockById(String blockId) {
        try {
            if (mc.level != null) {
                var registry = mc.level.registryAccess().lookup(Registries.BLOCK);
                if (registry.isPresent()) {
                    Optional<Block> block = registry.get().get(ResourceLocation.parse(blockId));
                    block.ifPresent(b -> { if (!searchBlocks.contains(b)) searchBlocks.add(b); });
                }
            }
        } catch (Exception ignored) {}
    }

    public void removeBlockById(String blockId) {
        searchBlocks.removeIf(b -> b.builtInRegistryHolder().key().location().toString().equals(blockId));
    }

    public void clearBlocks() {
        searchBlocks.clear();
    }
}
