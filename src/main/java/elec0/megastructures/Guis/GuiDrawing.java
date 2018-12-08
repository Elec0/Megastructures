package elec0.megastructures.Guis;

import elec0.megastructures.general.Constants;
import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GuiDrawing
{
	private static Random rand = new Random();

	/**
	 * Draw the systems when in sector view
	 * @param screen
	 * @param system
	 * @param squareSize
	 */
	public static void drawSystem(GuiScreen screen, SolarSystem system, int squareSize)
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

		screen.drawRect(2, 2, squareSize-2, squareSize-2, color);

	}

	public static void bindTexture(BufferedImage image)
	{
		BufferedTexture tex = new BufferedTexture();
		tex.loadTexture(image);
		GlStateManager.bindTexture(tex.getGlTextureId()); // This bypasses TextureUtil.bindTexture because it's private somehow? idk.
	}
}
