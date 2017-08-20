package elec0.megastructures.Guis;

import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.*;

public class GuiDrawing
{

	public static void drawSystem(GuiScreen screen, SolarSystem system, int squareSize)
	{
		//screen.drawRect(2, 2, squareSize - 4, squareSize - 4, 0xFFFFFFFF);
		GlStateManager.color(1, 1, 1, 1);
		glColor4f(1, 1, 1, 1);
		drawFilledCircle(squareSize / 2f, squareSize / 2f, 10);
	}


	/**
	 * Function that handles the drawing of a circle using the triangle fan
	 * method. This will create a filled circle.
	 *
	 *	x - the x position of the center point of the circle
	 *	y - the y position of the center point of the circle
	 *	radius - the radius that the painted circle will have
	 */
	private static void drawFilledCircle(double x, double y, double radius)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1f, 1f, 1f, 1f);
		bufferbuilder.begin(6, DefaultVertexFormats.POSITION);

		int triangleAmount = 20; //# of triangles used to draw circle
		double twicePi = 2.0f * Math.PI;

		bufferbuilder.pos(x, y, 0).endVertex(); // Center of circle

		for(int i = 0; i <= triangleAmount; i++)
		{
			bufferbuilder.pos(x + (radius * Math.cos(i *  twicePi / triangleAmount)),y + (radius * Math.sin(i * twicePi / triangleAmount)), 0).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
}
