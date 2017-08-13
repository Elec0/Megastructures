package elec0.megastructures.Guis;


import elec0.megastructures.Megastructures;
import elec0.megastructures.general.Vector2i;
import elec0.megastructures.network.PacketHandler;
import elec0.megastructures.network.PacketRequestTerminalData;
import elec0.megastructures.universe.Galaxy;
import elec0.megastructures.universe.Location;
import elec0.megastructures.universe.SolarSystem;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

public class TerminalGui extends GuiScreen
{
	private GuiButton sectorLeft, sectorRight, sectorUp, sectorDown, zoomOut, home, sectorGo;
	private GuiTextField sectorText;
	private Galaxy galaxy;
	private int zoom = 0; // 0 = galaxy overview, 1 = solar system overview, 2 = planet overview
	private int left, right, top, bottom;
	private int viewLeft, viewRight, viewTop, viewBottom, squareSize, viewSubsectors;
	private Vector2i viewSector;
	private Location viewLocation;

	private static final ResourceLocation background = new ResourceLocation(Megastructures.MODID, "textures/gui/terminal.png");
	private static final int w = 320, h = 150;
	private static final int PAD_HORIZ = 14, PAD_VERT = 14, BORDER_SIZE = 2, BORDER_VIEW = 3;
	private static int ID = 0;

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
		calcBounds();
		drawBackground();

		if(galaxy != null)
		{
			drawView();
			handleMouse(mouseX, mouseY);
			String str = null;
			if(zoom == 0)
				str = galaxy.getName();
			else if(zoom == 1)
				str = viewLocation.getName() + ": " + Location.positionToSubsector(viewLocation.getPosition());
			else if(zoom == 2)
				str = "";

			fontRenderer.drawString(str, viewLeft - fontRenderer.getStringWidth(str) - 7, top + 2, 0x000000);
		}

		sectorText.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks); // Handles drawing things like buttons, which need to be over the background

	}

	/**
	 * Calculate the relevant locations for the GUI, based on the size of the window
	 */
	private void calcBounds()
	{
		// Main gui bounds
		left = (width / 2) - (PAD_HORIZ + w/2);
		top = (height / 2) - (PAD_VERT + h/2);
		right = (width / 2) + (PAD_HORIZ + w/2);
		bottom = (height / 2) + (PAD_VERT + h/2);

		// Sector view bounds
		squareSize = (right-left) / 2;
		viewLeft = right - squareSize - BORDER_VIEW;
		viewTop = top + BORDER_VIEW;
		viewRight = viewLeft + squareSize - BORDER_VIEW;
		viewBottom = viewTop + squareSize - BORDER_VIEW;
		viewSubsectors = squareSize / Location.SUBSECTORS;
	}

	/***
	 * Draw the background for the custom GUI for the terminal, since it needs to be a lot larger than the max MC thinks a GUI should be: 320x240
	 */
	private void drawBackground()
	{
		drawRect(left - BORDER_SIZE, top - BORDER_SIZE, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFFFFFFFF); // White
		drawRect(left, top, right + BORDER_SIZE, bottom + BORDER_SIZE, 0xFF000000); // Black
		drawRect(left, top, right, bottom,0xFFC6C6C6); // Default MC background

	}

	/**
	 * Draw all the interesting bits of the GUI: Sector view, planets, etc
	 */
	private void drawView()
	{

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

					fontRenderer.drawString("SS", viewLeft + subsector.getX() * viewSubsectors + 3, viewTop + subsector.getY() * viewSubsectors + 5, 0xFF0000);
				}
				break;

			case 1: // System view

				break;

			case 2: // Planet view

				break;
		}

		// Draw current sector location
		//fontRenderer.drawString(viewSector.toString(), viewLeft - fontRenderer.getStringWidth(viewSector.toString()), viewTop + 20, 0x000000, false);
	}

	/**
	 * Handles mouse events that aren't clicking
	 */
	private void handleMouse(int mouseX, int mouseY)
	{
		boolean inView = (mouseX > viewLeft && mouseX < viewRight) && (mouseY > viewTop && mouseY < viewBottom);
		if(inView)
		{
			Vector2i mouseSub = mouseToSubsector(mouseX, mouseY);

			// Make sure the calculated subsector actually makes sense
			if(mouseSub.getX() >= 0 && mouseSub.getX() < Location.SUBSECTORS && mouseSub.getY() >= 0 && mouseSub.getY() < Location.SUBSECTORS)
			{
				if(zoom == 0)
					drawSystemInfo(mouseSub.getX(), mouseSub.getY());
			}
		}
	}

	/**
	 * Draw the rectangle that shows solar system information in the sector view
	 * @param subX
	 * @param subY
	 */
	private void drawSystemInfo(int subX, int subY)
	{
		// 4 squares width, 2 squares hieght
		int INFO_WIDTH = 4 * viewSubsectors, INFO_HEIGHT = 2 * viewSubsectors, PAD_LEFT = 2, PAD_TOP = 2;
		int left = viewLeft + subX * viewSubsectors + PAD_LEFT, top = viewTop + subY * viewSubsectors + PAD_TOP;

		SolarSystem sys = getSystemSubsector(viewSector, subX, subY);
		if(sys != null)
		{
			drawRect(left, top, left + INFO_WIDTH + PAD_LEFT, top + INFO_HEIGHT + PAD_TOP, 0xBA9B9B9B);
			fontRenderer.drawSplitString(sys.getName() + " " + Location.positionToSubsector(sys.getPosition()), left + PAD_LEFT, top + PAD_TOP, INFO_WIDTH,0x000000);

		}
	}


	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	@Override
	public void initGui()
	{
		calcBounds();
		int btnBorder = 6;
		int btnSize = fontRenderer.getStringWidth("<-") + 4;
		buttonList.add(sectorLeft = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder) * 2, height / 2, btnSize + btnBorder, 20, "◄"));
		buttonList.add(sectorRight = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), height / 2, btnSize + btnBorder, 20, "►"));
		buttonList.add(sectorUp = new GuiButton(nextID(), viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) - 20, btnSize + btnBorder, 20, "▲"));
		buttonList.add(sectorDown = new GuiButton(nextID(), viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) + 20, btnSize + btnBorder, 20, "▼"));

		btnSize = fontRenderer.getStringWidth("Zoom Out");
		buttonList.add(zoomOut = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), (height / 2) + 40, btnSize + btnBorder, 20, "Zoom Out"));

		btnSize = fontRenderer.getStringWidth("Home");
		buttonList.add(home = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder) - 7, (height / 2) - 40, btnSize + btnBorder, 20, "Home"));

		btnSize = fontRenderer.getStringWidth("Go");
		buttonList.add(sectorGo = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), viewTop + 10, btnSize + btnBorder, 20, "Go"));
		int txtSize = fontRenderer.getStringWidth("-100, -100");
		sectorText = new GuiTextField(nextID(), fontRenderer, viewLeft - (txtSize + (int)(btnBorder*1.5) + btnSize), viewTop + 10, txtSize, 20);
		sectorText.setMaxStringLength(30);
		//sectorText.setCanLoseFocus(true);


		// To handle when the GUI is resized, everything is cleared so need to be re-initialized
		if(viewSector != null)
			sectorText.setText(viewSector.getX() + ", " + viewSector.getY());
		else if(galaxy != null)
			sectorText.setText(galaxy.getSector().getX() + ", " + galaxy.getSector().getY());

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		// Close the gui
			/*mc.displayGuiScreen(null);
			if (mc.currentScreen == null)
				mc.setIngameFocus();
				*/
		if (button == sectorLeft)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX() - 1, viewSector.getY())));
		}
		if (button == sectorRight)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX() + 1, viewSector.getY())));
		}
		if (button == sectorUp)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX(), viewSector.getY() + 1)));
		}
		if (button == sectorDown)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX(), viewSector.getY() - 1)));
		}
		if(button == sectorGo)
		{
			String[] input = sectorText.getText().split(",");
			Vector2i newSector = viewSector; // Just to make sure we have a value
			if(input.length == 2)
			{
				try
				{
					newSector = new Vector2i(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim()));
					zoom = 0; // Switch back to sector view if they put in a sector, but not if the arrows are pushed
				} catch (Exception e){
				}
			}
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(newSector));
		}
		if(button == home)
		{
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(galaxy.getSector()));
		}
		if(button == zoomOut)
		{
			if(zoom > 0)
				zoom -= 1;
		}

	}

	/**
	 *
	 * @param typedChar
	 * @param keyCode
	 */
	@Override
	protected void  keyTyped(char typedChar, int keyCode)
	{
		try
		{
			super.keyTyped(typedChar, keyCode);
		}
		catch(IOException e) {		}

		if(sectorText.isFocused())
			sectorText.textboxKeyTyped(typedChar, keyCode);
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param btn
	 */
	@Override
	protected void mouseClicked(int x, int y, int btn)
	{
		try
		{
			super.mouseClicked(x, y, btn);
		}
		catch(IOException e) { }

		sectorText.mouseClicked(x, y, btn);

		boolean inView = (x > viewLeft && x < viewRight) && (y > viewTop && y < viewBottom);
		if(inView)
		{
			Vector2i mouseSub = mouseToSubsector(x, y);
			if(zoom == 0) // We're looking at solar systems
			{
				SolarSystem sys = getSystemSubsector(viewSector, mouseSub.getX(), mouseSub.getY());
				if(sys != null)
				{
					viewLocation = sys;
					zoom = 1;
				}
			}

		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		sectorText.updateCursorCounter();
	}

	/**
	 * Called by PacketSendTerminalData when the packet is finished being received
	 */
	public void packedFinished()
	{
		// Have to wait till after the packet is received to set the text box
		String text = "";
		if(viewSector != null)
			text = viewSector.getX() + ", " + viewSector.getY();
		else if(galaxy != null) // If this isn't true we have problems
			text = galaxy.getSector().getX() + ", " + galaxy.getSector().getY();
		sectorText.setText(text);
	}

	public void setGalaxy(Galaxy galaxy)
	{
		this.galaxy = galaxy;
	}

	public void setViewSector(Vector2i viewSector)
	{
		this.viewSector = viewSector;
	}

	private static int nextID() { return ID++; }

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}


	// ----------------- Utility Methods -----------------

	/**
	 * Given the sector and subsector x & y, return a system in that sector at the subsector location
	 * Returns null if there is no system at that location
	 * @param sector
	 * @param subX
	 * @param subY
	 * @return
	 */
	public SolarSystem getSystemSubsector(Vector2i sector, int subX, int subY)
	{
		List<SolarSystem> list = galaxy.getSectorList(sector);
		for(int i = 0; i < list.size(); ++i)
		{
			Vector2i subsector = Location.positionToSubsector(list.get(i).getPosition());
			if(subsector.getX() == subX && subsector.getY() == subY)
			{
				return list.get(i);
			}
		}

		return null;
	}
	/**
	 * Calculate the subsector square the mouse is in
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	private Vector2i mouseToSubsector(int mouseX, int mouseY)
	{
		return new Vector2i((mouseX - viewLeft) / viewSubsectors, (mouseY - viewTop) / viewSubsectors);
	}
}
