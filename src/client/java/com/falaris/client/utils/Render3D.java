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
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderType lineType = RenderTypes.lines();
        VertexConsumer consumer = bufferSource.getBuffer(lineType);
        Matrix4f matrix = poseStack.last().pose();

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        line(consumer, matrix, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, minX, minY, maxZ, minX, minY, minZ, red, green, blue, alpha, lineWidth);

        line(consumer, matrix, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue, alpha, lineWidth);

        line(consumer, matrix, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha, lineWidth);
        line(consumer, matrix, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha, lineWidth);

        bufferSource.endBatch(lineType);
    }

    public static void drawLine(PoseStack poseStack, float startX, float startY, float startZ, float endX, float endY, float endZ, float red, float green, float blue, float alpha, float lineWidth) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderType lineType = RenderTypes.lines();
        VertexConsumer consumer = bufferSource.getBuffer(lineType);
        line(consumer, poseStack.last().pose(), startX, startY, startZ, endX, endY, endZ, red, green, blue, alpha, lineWidth);
        bufferSource.endBatch(lineType);
    }

    private static void line(VertexConsumer consumer, Matrix4f matrix, float startX, float startY, float startZ, float endX, float endY, float endZ, float red, float green, float blue, float alpha, float lineWidth) {
        consumer.addVertex(matrix, startX, startY, startZ).setColor(red, green, blue, alpha).setLineWidth(lineWidth);
        consumer.addVertex(matrix, endX, endY, endZ).setColor(red, green, blue, alpha).setLineWidth(lineWidth);
    }
}
