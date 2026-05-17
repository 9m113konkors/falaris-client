package com.falaris.client.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public final class Render3D {
    private Render3D() {}

    public static void drawBox(PoseStack poseStack, AABB box, float red, float green, float blue, float alpha, float lineWidth) {
        // No-op drawing to avoid rendering API mismatches in this environment.
        // Proper 3D line rendering can be implemented later using the game's VertexConsumer API.
    }

    public static void drawLine(PoseStack poseStack, float startX, float startY, float startZ, float endX, float endY, float endZ, float red, float green, float blue, float alpha, float lineWidth) {
        // No-op
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix, float startX, float startY, float startZ, float endX, float endY, float endZ, float red, float green, float blue, float alpha) {
        // No-op
    }
}
