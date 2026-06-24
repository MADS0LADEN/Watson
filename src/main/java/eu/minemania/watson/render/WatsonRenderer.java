package eu.minemania.watson.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.selection.EditSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4fStack;

public class WatsonRenderer
{
    private static final WatsonRenderer INSTANCE = new WatsonRenderer();

    private Minecraft mc;

    public static WatsonRenderer getInstance()
    {
        return INSTANCE;
    }

    public void piecewiseRenderEntities(Minecraft mc, ProfilerFiller profiler, GpuBufferSlice fog)
    {
        if (this.mc == null)
        {
            this.mc = mc;
        }
        if (Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getCameraEntity() != null && Configs.Outlines.OUTLINE_SHOWN.getBooleanValue())
        {
            profiler.push(() -> "watson_entities");
            GpuBufferSlice savedFog = RenderSystem.getShaderFog();
            RenderSystem.setShaderFog(null);
            EditSelection selection = DataManager.getEditSelection();
            BlockEditSet edits = selection.getBlockEditSet();
            Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.pushMatrix();

            Vec3 cameraPos = this.mc.gameRenderer.getMainCamera().position();

            matrixStack.translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);
            edits.drawOutlines();
            edits.drawVectors();
            selection.drawSelection();

            matrixStack.popMatrix();
            RenderSystem.setShaderFog(savedFog != null ? savedFog : fog);
            profiler.pop();
        }
    }
}
