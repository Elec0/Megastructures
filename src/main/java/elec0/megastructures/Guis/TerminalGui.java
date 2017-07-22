package elec0.megastructures.Guis;


import elec0.megastructures.universe.Galaxy;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class TerminalGui extends GuiScreen
{
	private GuiButton a, b;
	private Galaxy galaxy;

	/*
		drawScreen is called every frame, I believe. Or close enough to not matter.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(galaxy != null)
		{
			this.fontRenderer.drawString(String.valueOf(galaxy.getSeed()), 0, 0, 0xFF0000, false);
		}
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

	public void updateInfo()
	{

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
