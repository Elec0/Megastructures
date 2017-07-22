package elec0.megastructures.Guis;


import elec0.megastructures.Megastructures;
import elec0.megastructures.universe.Galaxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class TerminalGui extends GuiScreen
{
	private GuiButton a, b;
	private Galaxy galaxy;

	private static final ResourceLocation background = new ResourceLocation(Megastructures.MODID, "textures/gui/terminal.png");

	public TerminalGui()
	{

	}

	/*
		drawScreen is called every frame, I believe. Or close enough to not matter.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		drawBackground();

		if(galaxy != null)
		{
			this.fontRenderer.drawString(String.valueOf(galaxy.getSeed()), 0, 0, 0xFF0000, false);
		}

		super.drawScreen(mouseX, mouseY, partialTicks); // Handles drawing things like buttons, which need to be over the background
	}

	/***
	 * Draw the background for the custom GUI for the terminal, since it needs to be a lot larger than the max MC thinks a GUI should be: 320x240
	 */
	private void drawBackground()
	{
		int w = 320, h = 150;
		int PAD_HORIZ = 14, PAD_VERT = 14, BORDER_SIZE = 2;
		int left = (width / 2) - (PAD_HORIZ + w/2), top = (height / 2) - (PAD_VERT + h/2);
		int right = (width / 2) + (PAD_HORIZ + w/2), bottom = (height / 2) + (PAD_VERT + h/2);

		this.drawRect(left - BORDER_SIZE, top - BORDER_SIZE, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFFFFFFFF); // White
		this.drawRect(left, top, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFF000000); // Black
		this.drawRect(left, top, right, bottom,0xFFC6C6C6); // Default MC background

	}

	@Override
	public void initGui()
	{
		this.buttonList.add(this.a = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "This is button a"));
		this.buttonList.add(this.b = new GuiButton(1, this.width / 2 - 100, this.height / 2 + 4, "This is button b"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == this.a)
		{
			//Main.packetHandler.sendToServer(...);
			this.mc.displayGuiScreen(null);
			if (this.mc.currentScreen == null)
				this.mc.setIngameFocus();
		}
		if (button == this.b)
		{
			//Main.packetHandler.sendToServer(...);
			this.mc.displayGuiScreen(null);
			if (this.mc.currentScreen == null)
				this.mc.setIngameFocus();
		}
	}

	public void setGalaxy(Galaxy galaxy)
	{
		this.galaxy = galaxy;
	}


	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
