package elec0.megastructures.Guis;

import elec0.megastructures.general.Constants;
import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GuiDrawing
{
	private static Random rand = new Random();

	/**
	 * Draw the systems when in sector view
	 * Transorm matix has already happened
	 * @param screen
	 * @param system
	 * @param circleRad
	 */
	public static void drawSystem(GuiScreen screen, SolarSystem system, int circleRad)
	{
		//screen.drawRect(2, 2, squareSize - 4, squareSize - 4, 0xFFFFFFFF);

		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLUE);
		g2d.fill(new Ellipse2D.Float(0, 0, 32, 32));
		g2d.dispose();

		//bindTexture(image);
		//screen.drawTexturedModalRect(0, 0, 0, 0, squareSize, squareSize);

		// Generate color
		int a = 255;

		// Just so the numbers are consistent, and we don't have too much relevant overlap
		long seed = system.getSeed() + Constants.SEED_OFFSET_GUI_SYSTEM;
		// Don't create a new object every time, this runs every tick so that'd be slow.
		rand.setSeed(seed);

		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);

		int color = ((a & 0xFF) << 24) |
					((r & 0xFF) << 16) |
					((g & 0xFF) << 8)  |
					((b & 0xFF) << 0);

		//GuiScreen.drawRect(2, 2, squareSize-2, squareSize-2, color);

		// Draw the circle in the square. Have to tweak the coords a little bit
		drawCircle(circleRad + 2, circleRad + 2, circleRad, color);

	}

	public static void drawCircle(int centerX, int centerY, int radius, int color) {
		drawEllipse(centerX, centerY, radius, radius, 20, color);
	}

	public static void bindTexture(BufferedImage image)
	{
		BufferedTexture tex = new BufferedTexture();
		tex.loadTexture(image);
		GlStateManager.bindTexture(tex.getGlTextureId()); // This bypasses TextureUtil.bindTexture because it's private somehow? idk.
	}


	/**
	 * Draws a solid color rectangle with the specified coordinates and color.
	 */
	public static void drawEllipse(int centerX, int centerY, int radX, int radY, int numSegments, int color)
	{
		// I removed the error-checking, so don't be an idiot

		// Handle color shit
		float f3 = (float)(color >> 24 & 255) / 255.0F;
		float f = (float)(color >> 16 & 255) / 255.0F;
		float f1 = (float)(color >> 8 & 255) / 255.0F;
		float f2 = (float)(color & 255) / 255.0F;

		// Tessellator because drawing in minecraft is complicated
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		// Need to make another method if we want to draw textures on this
		GlStateManager.disableTexture2D();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		// Set the color
		GlStateManager.color(f, f1, f2, f3);

		float theta = 2 * 3.1415926f / (float)numSegments;
		float c = (float)Math.cos(theta); // Precalculate the sine and cosine
		float s = (float)Math.sin(theta);
		float t;

		float x = 1; // We start at angle = 0
		float y = 0;

		bufferbuilder.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);

		// Taken from https://stackoverflow.com/questions/5886628/effecient-way-to-draw-ellipse-with-opengl-or-d3d
		// Because I can't be assed to re-derive this
		for(int ii = 0; ii < numSegments; ii++)
		{
			// Apply radius and offset
			bufferbuilder.pos(x * radX + centerX, y * radY + centerY, 0.0D).endVertex();

			// Apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}

		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
}
