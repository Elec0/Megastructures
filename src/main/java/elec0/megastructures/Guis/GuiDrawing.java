package elec0.megastructures.Guis;

import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class GuiDrawing
{

	public static void drawSystem(GuiScreen screen, SolarSystem system, int squareSize)
	{
		//screen.drawRect(2, 2, squareSize - 4, squareSize - 4, 0xFFFFFFFF);

		BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLUE);
		g2d.fill(new Ellipse2D.Float(0, 0, 32, 32));
		g2d.dispose();

		bindTexture(image);

		screen.drawTexturedModalRect(0, 0, 0, 0, squareSize, squareSize);

	}

	public static void bindTexture(BufferedImage image)
	{
		BufferedTexture tex = new BufferedTexture();
		tex.loadTexture(image);
		GlStateManager.bindTexture(tex.getGlTextureId()); // This bypasses TextureUtil.bindTexture because it's private somehow? idk.
	}
}
