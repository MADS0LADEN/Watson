package eu.minemania.watson.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import fi.dy.masa.malilib.render.MaLiLibPipelines;
import fi.dy.masa.malilib.render.RenderContext;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class RenderUtils
{
    public static BufferBuilder startDrawingLines(Tesselator tessellator)
    {
        return tessellator.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
    }

    public static void drawMesh(MeshData meshData)
    {
        if (meshData == null)
        {
            return;
        }

        try (RenderContext context = new RenderContext(() -> "watson", MaLiLibPipelines.DEBUG_LINES_MASA_SIMPLE))
        {
            context.draw(meshData, false);
        }
        catch (Exception e)
        {
            meshData.close();
        }
    }

    //START TEMP MALILIB
    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3.ZERO, color, expand, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Vec3 cameraPos, Color4f color, double expand, BufferBuilder buffer)
    {
        float minX = (float) (pos.getX() - expand - cameraPos.x);
        float minY = (float) (pos.getY() - expand - cameraPos.y);
        float minZ = (float) (pos.getZ() - expand - cameraPos.z);
        float maxX = (float) (pos.getX() + expand - cameraPos.x + 1);
        float maxY = (float) (pos.getY() + expand - cameraPos.y + 1);
        float maxZ = (float) (pos.getZ() + expand - cameraPos.z + 1);

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBoxAllEdgesBatchedLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                                                   Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        // East side
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.addVertex(maxX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, minY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(minX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, maxY, minZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.addVertex(minX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(maxX, minY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(maxX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(minX, maxY, maxZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
    }
    //END TEMP MALILIB

    public static void drawFullBlockOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer)
    {
        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + 1F, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + 1F, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + 1F, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y + 1F, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + 1F, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y + 1F, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + 1F, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y + 1F, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + 1F, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + 1F, y, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + 1F, y + 1, z + 1F).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
    }

    public static void drawSpecialOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer, boolean sign)
    {
        float posX = x + 0.25F / 2;
        float posY = y + 0.25F / 2;
        float posZ = z + 0.015F;
        float widthX = (12 / 32.0F) * 2;
        float heightY = (12 / 32.0F) * 2;
        float widthZ = (1.0F / 32.0F) * 2;

        if (sign)
        {
            posX = posX - 0.1F;
            posY = posY + 0.2F;
            widthX = widthX + 0.2F;
            heightY = heightY - 0.3F;
        }

        buffer.addVertex(posX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX + widthX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX + widthX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX + widthX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX, posY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX + widthX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX + widthX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(posX, posY + heightY, posZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(posX, posY + heightY, posZ + widthZ).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
    }

    public static void drawBedOutlineBatched(float x, float y, float z, Color4f color, BufferBuilder buffer)
    {
        float shortLength = 0.19f;
        float reverseShortLength = 0.81f;
        float otherSide = 1f;
        float longHeight = 0.56f;

        //left front leg
        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + shortLength, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + shortLength, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + shortLength, y + shortLength, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + shortLength, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        //right front leg
        buffer.addVertex(x + reverseShortLength, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + reverseShortLength, y, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + reverseShortLength, y + shortLength, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + shortLength, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        //left back leg
        buffer.addVertex(x, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + shortLength, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + shortLength, y, z + 1).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + shortLength, y + shortLength, z + 1).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + shortLength, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        //right back leg
        buffer.addVertex(x + reverseShortLength, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + reverseShortLength, y, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + reverseShortLength, y + shortLength, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + shortLength, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        //middle connections
        buffer.addVertex(x + shortLength, y + shortLength, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + reverseShortLength, y + shortLength, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + shortLength, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + shortLength, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + shortLength, y + shortLength, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + reverseShortLength, y + shortLength, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y + shortLength, z + shortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + shortLength, z + reverseShortLength).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        //top connections
        buffer.addVertex(x, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);

        buffer.addVertex(x + otherSide, y + longHeight, z).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
        buffer.addVertex(x + otherSide, y + longHeight, z + otherSide).setColor(color.r, color.g, color.b, color.a).setNormal(0, 0, 0);
    }

    public static void drawBlockModelOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer)
    {
        drawFullBlockOutlinesBatched(x, y, z, color, buffer);
    }
}