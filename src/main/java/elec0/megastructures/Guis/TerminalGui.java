package elec0.megastructures.Guis;


import elec0.megastructures.Megastructures;
import elec0.megastructures.general.Vector2i;
import elec0.megastructures.universe.Galaxy;
import elec0.megastructures.universe.Location;
import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

public class TerminalGui extends GuiScreen
{
	//private GuiButton a, b;
	private Galaxy galaxy;
	private int zoom = 0; // 0 = galaxy overview, 1 = solar system overview, 2 = planet overview
	private int left, right, top, bottom;
	private Vector2i viewSector;

	private static final ResourceLocation background = new ResourceLocation(Megastructures.MODID, "textures/gui/terminal.png");
	private static final int w = 320, h = 150;
	private static final int PAD_HORIZ = 14, PAD_VERT = 14, BORDER_SIZE = 2;

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
			drawView();
			fontRenderer.drawString(galaxy.getName() + "," + galaxy.getSector().toString(), 0, 0, 0xFF0000, false);
		}

		super.drawScreen(mouseX, mouseY, partialTicks); // Handles drawing things like buttons, which need to be over the background
	}

	/***
	 * Draw the background for the custom GUI for the terminal, since it needs to be a lot larger than the max MC thinks a GUI should be: 320x240
	 */
	private void drawBackground()
	{
		left = (width / 2) - (PAD_HORIZ + w/2);
		top = (height / 2) - (PAD_VERT + h/2);
		right = (width / 2) + (PAD_HORIZ + w/2);
		bottom = (height / 2) + (PAD_VERT + h/2);

		drawRect(left - BORDER_SIZE, top - BORDER_SIZE, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFFFFFFFF); // White
		drawRect(left, top, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFF000000); // Black
		drawRect(left, top, right, bottom,0xFFC6C6C6); // Default MC background

	}

	private void drawView()
	{
		int BORDER_VIEW = 4;
		int squareSize = (right-left) / 2;
		int viewSubsectors = squareSize / Location.SUBSECTORS;
		int viewLeft = right - squareSize - BORDER_VIEW, viewTop = top + BORDER_VIEW, viewRight = right - BORDER_VIEW, viewBottom = top + squareSize;

		drawRect(viewLeft, viewTop, viewRight, viewBottom, 0xFF000000);

		// Draw sector grid
		for(int i = 0; i < Location.SUBSECTORS + 1; ++i)
		{
			drawHorizontalLine(viewLeft, viewRight, viewTop + i * viewSubsectors, 0xFFFFFFFF);
			drawVerticalLine(viewLeft + i * viewSubsectors, viewTop, viewBottom, 0xFFFFFFFF);
		}

		switch(zoom)
		{
			case 0: // Sector view
				List<SolarSystem> sector = galaxy.getSectorList(viewSector);
				for(int i = 0; i < sector.size(); ++i)
				{
					SolarSystem s = sector.get(i);
					Vector2i subsector = Location.positionToSubsector(s.getPosition());

					System.out.println(s.getPosition() + ", " + s.getSector() + ", " + subsector);
					//[17:02:22] [main/INFO]: [STDOUT]: [-1064328987, 1995612844], [-106432, 199561], [1064222555, 2844]
					//[17:02:22] [main/INFO]: [STDOUT]: [-1064320000, 1995610000], [-106432, 199561], [1064213568, 0]

					//fontRenderer.drawString("SS", viewLeft + subsector.getX() * viewSubsectors, viewTop + subsector.getY() * viewSubsectors, 0xFF0000, false);
					// System.out.println((viewLeft + subsector.getX() * viewSubsectors) + ", " + (viewTop + subsector.getY() * viewSubsectors));
					//fontRenderer.drawString(s.getName() + ", " + s.getPosition().toString() + ", " + s.getSectorList().toString(), 0, 10*(i+1), 0xFF0000, false);
				}
				break;
		}


	}

	@Override
	public void initGui()
	{
		//this.buttonList.add(this.a = new GuiButton(0, this.width / 2 - 100, this.height / 2 - 24, "This is button a"));
		//this.buttonList.add(this.b = new GuiButton(1, this.width / 2 - 100, this.height / 2 + 4, "This is button b"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		/*if (button == this.a)
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
		}*/
	}

	public void setGalaxy(Galaxy galaxy)
	{
		this.galaxy = galaxy;
		viewSector = galaxy.getSector();
	}


	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
}
