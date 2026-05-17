package com.falaris.client.modules.render;

import com.falaris.client.modules.Category;
import com.falaris.client.modules.Module;
import com.falaris.client.modules.setting.BooleanSetting;
import com.falaris.client.modules.setting.ColorSetting;
import com.falaris.client.utils.Render3D;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Tracers extends Module {
    public final ColorSetting color = addSetting(new ColorSetting("Color", "Tracer line color", 0xFFFFA31A));
    public final BooleanSetting showTeammates = addSetting(new BooleanSetting("Show Teammates", "Render teammates in tracers", true));

    public Tracers() { super("Tracers", "Renders lines to players", Category.VISUAL, "lines"); }

    @Override
    public void onRender3D(Camera camera, float tickDelta) {
        if (mc.level == null || mc.player == null) return;
        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.position();
        Vec3 eyes = new Vec3(0, mc.player.getEyeHeight(), 0);

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player player) || player == mc.player) continue;
            if (!showTeammates.getValue() && player.isAlliedTo(mc.player)) continue;

            double x = player.xo + (player.getX() - player.xo) * tickDelta - cameraPos.x;
            double y = player.yo + (player.getY() - player.yo) * tickDelta - cameraPos.y;
            double z = player.zo + (player.getZ() - player.zo) * tickDelta - cameraPos.z;

            Render3D.drawLine(poseStack, (float)eyes.x, (float)eyes.y, (float)eyes.z, (float)x, (float)(y + player.getEyeHeight()/2), (float)z, color.red(), color.green(), color.blue(), color.alpha(), 1.0f);
        }
    }
}
