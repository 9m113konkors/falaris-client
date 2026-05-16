package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.ColorSetting;
import com.falaris.client.modules.setting.EnumSetting;
import com.falaris.client.utils.Render3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Tracers extends Module {
    public enum OriginMode {
        CAMERA,
        CHEST
    }

    public final ColorSetting color = addSetting(new ColorSetting("Color", "Tracer color", 0xFFFFA31A));
    public final BooleanSetting showTeammates = addSetting(new BooleanSetting("Show Teammates", "Render teammates in tracers", false));
    public final EnumSetting<OriginMode> originMode = addSetting(new EnumSetting<>("Origin", "Where tracer lines begin", OriginMode.CAMERA, OriginMode.values()));

    public Tracers() {
        super("Tracers", "Draws lines to players", Category.VISUAL, "lines", "player lines");
    }

    @Override
    public void onRender3D(Camera camera, float tickDelta) {
        if (mc.level == null || mc.player == null) {
            return;
        }

        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.position();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player player) || player == mc.player) {
                continue;
            }
            if (!showTeammates.getValue() && player.isAlliedTo(mc.player)) {
                continue;
            }

            float startX = 0.0f;
            float startY = originMode.getValue() == OriginMode.CHEST ? mc.player.getBbHeight() * 0.6f : 0.0f;
            float startZ = 0.0f;
            float endX = (float) (player.xo + (player.getX() - player.xo) * tickDelta - cameraPos.x);
            float endY = (float) (player.yo + (player.getY() - player.yo) * tickDelta - cameraPos.y + player.getBbHeight() * 0.5f);
            float endZ = (float) (player.zo + (player.getZ() - player.zo) * tickDelta - cameraPos.z);

            Render3D.drawLine(poseStack, startX, startY, startZ, endX, endY, endZ, color.red(), color.green(), color.blue(), color.alpha(), 1.5f);
        }
    }
}
