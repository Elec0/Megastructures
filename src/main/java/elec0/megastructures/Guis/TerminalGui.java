package elec0.megastructures.Guis;


import elec0.megastructures.Megastructures;
import elec0.megastructures.general.Vector2i;
import elec0.megastructures.general.Vector2l;
import elec0.megastructures.network.PacketHandler;
import elec0.megastructures.network.PacketRequestTerminalData;
import elec0.megastructures.universe.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;

public class TerminalGui extends GuiScreen
{
	private GuiButton sectorLeft, sectorRight, sectorUp, sectorDown, zoomOut, home, sectorGo, toggleGrid;
	private GuiTextField sectorText;
	private Galaxy galaxy;
	private int zoom = 0; // 0 = galaxy overview, 1 = solar system overview, 2 = planet overview
	private int left, right, top, bottom;
	private int viewLeft, viewRight, viewTop, viewBottom, squareSize, viewSubsectors, viewSubsystems;
	private Vector2i viewSector;
	private Location viewLocation;
	private boolean displayGrid = true;

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
		viewSubsystems = squareSize / Location.SUBSYSTEMS;
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

		// Draw the grid, if it's enabled. Less duplicate code this way
		int[] gridSize = new int[] {Location.SUBSECTORS, Location.SUBSYSTEMS, 0};
		drawGrid(gridSize[zoom]);

		switch(zoom)
		{
			case 0: // Sector view
				List<SolarSystem> sector = galaxy.getSectorList(viewSector);
				for(int i = 0; i < sector.size(); ++i)
				{
					SolarSystem s = sector.get(i);
					Vector2i subsector = Location.positionToSubsector(s.getPosition());

					GL11.glPushMatrix();
					GL11.glTranslated(viewLeft + subsector.getX() * viewSubsectors, viewTop + subsector.getY() * viewSubsectors, 0);
					//fontRenderer.drawString("SS", 0, 0, 0xFF0000);
					GuiDrawing.drawSystem(this, s, viewSubsectors);
					GL11.glPopMatrix();
				}
				break;

			case 1: // System view
				List<Celestial> system = ((SolarSystem) viewLocation).getCelestials();
				for(int i = 0; i < system.size(); ++i)
				{
					Celestial c = system.get(i);
					Vector2i subsystem = Location.positionToSubsystem(c.getPosition());

					GL11.glPushMatrix();
					GL11.glTranslated(viewLeft + subsystem.getX() * viewSubsystems + 2, viewTop + subsystem.getY() * viewSubsystems + 1, 0);
					if(c instanceof Planet)
						fontRenderer.drawString("P", 0, 0, 0xFF0000);
					else if(c instanceof Star)
						fontRenderer.drawString("S", 0, 0, 0xFF0000);

					GL11.glPopMatrix();
				}
				break;

			case 2: // Planet view

				break;
		}

		// Draw current sector location
		//fontRenderer.drawString(viewSector.toString(), viewLeft - fontRenderer.getStringWidth(viewSector.toString()), viewTop + 20, 0x000000, false);
	}

	/**
	 * Draw the grid, if it's enabled.
	 * Subsustems for system
	 * Subsectors for sector
	 * @param numSquares
	 */
	private void drawGrid(int numSquares)
	{
		if(displayGrid)
		{
			// Draw system grid
			int viewNum = squareSize / numSquares;

			for (int i = 0; i < numSquares + 1; ++i)
			{
				drawHorizontalLine(viewLeft, viewRight, viewTop + i * viewNum, 0xFFFFFFFF);
				drawVerticalLine(viewLeft + i * viewNum, viewTop, viewBottom, 0xFFFFFFFF);
			}
		}
	}


	/**
	 * Handles mouse events that aren't clicking
	 */
	private void handleMouse(int mouseX, int mouseY)
	{
		// Determine if the mouse is in the viewport
		boolean inView = (mouseX > viewLeft && mouseX < viewRight) && (mouseY > viewTop && mouseY < viewBottom);
		if(inView)
		{
			// If so, turn the mouse location to a subsector and display the relevant information
			Vector2i mouseSub = mouseToSubsector(mouseX, mouseY);
			drawInfo(zoom, mouseSub.getX(), mouseSub.getY());
		}
	}

	/**
	 * Draw the rectangle that shows celestial information in the sector/solar system/planet view
	 * @param zoom
	 * @param subX
	 * @param subY
	 */
	private void drawInfo(int zoom, int subX, int subY)
	{
		// Keep same sizing as on system map (INFOs)
		// 4 squares width, 2 squares hieght
		int INFO_WIDTH = 4 * viewSubsectors, INFO_HEIGHT = 2 * viewSubsectors, PAD_LEFT = 2, PAD_TOP = 2;
		int left = viewLeft + subX * viewSubsystems + PAD_LEFT, top = viewTop + subY * viewSubsystems + PAD_TOP;

		Location loc = null;
		Vector2i position = null;
		Vector2i.Vector2iInterface vecInt = null;

		if(zoom == 0) // Celestials
		{
			loc = getSystemSubsector(viewSector, subX, subY);
			// Functional programming, finally
			vecInt = Location::positionToSubsector;
		}
		else if(zoom == 1) // Solar Systems
		{
			loc = getCelestialSubsystem(subX, subY);
			vecInt = Location::positionToSubsystem;
		}
		else if (zoom == 2) // Planets
		{}

		if(loc != null)
		{
			position = vecInt.position(loc.getPosition());
			drawRect(left, top, left + INFO_WIDTH + PAD_LEFT, top + INFO_HEIGHT + PAD_TOP, 0xBA9B9B9B);
			fontRenderer.drawSplitString(loc.getName() + " " + position, left + PAD_LEFT, top + PAD_TOP, INFO_WIDTH,0x000000);
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
		int btnBorder = 6, btnHeight = 20;
		int btnSize = fontRenderer.getStringWidth("<-") + 4;
		buttonList.add(sectorLeft = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder) * 2, height / 2, btnSize + btnBorder, btnHeight, "◄"));
		buttonList.add(sectorRight = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), height / 2, btnSize + btnBorder, btnHeight, "►"));
		buttonList.add(sectorUp = new GuiButton(nextID(), viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) - 20, btnSize + btnBorder, btnHeight, "▲"));
		buttonList.add(sectorDown = new GuiButton(nextID(), viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) + 20, btnSize + btnBorder, btnHeight, "▼"));

		btnSize = fontRenderer.getStringWidth("Zoom Out");
		buttonList.add(zoomOut = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), (height / 2) + btnHeight * 2, btnSize + btnBorder, btnHeight, "Zoom Out"));

		btnSize = fontRenderer.getStringWidth("Home");
		buttonList.add(home = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder) - 7, (height / 2) - btnHeight * 2, btnSize + btnBorder, btnHeight, "Home"));

		btnSize = fontRenderer.getStringWidth("Toggle Grid");
		buttonList.add(toggleGrid = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), (height / 2) + btnHeight * 3, btnSize + btnBorder, btnHeight, "Toggle Grid"));


		btnSize = fontRenderer.getStringWidth("Go");
		buttonList.add(sectorGo = new GuiButton(nextID(), viewLeft - (btnSize + btnBorder), viewTop + 10, btnSize + btnBorder, btnHeight, "Go"));

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

	/**
	 * Button clicking
	 * @param button
	 * @throws IOException
	 */
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		// Close the gui
			/*mc.displayGuiScreen(null);
			if (mc.currentScreen == null)
				mc.setIngameFocus();
				*/

		if (button == sectorLeft) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX() - 1, viewSector.getY())));
		}
		else if (button == sectorRight) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX() + 1, viewSector.getY())));
		}
		else if (button == sectorUp) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX(), viewSector.getY() + 1)));
		}
		else if (button == sectorDown) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(new Vector2i(viewSector.getX(), viewSector.getY() - 1)));
		}
		else if(button == sectorGo) {
			String[] input = sectorText.getText().split(",");
			Vector2i newSector = viewSector; // Just to make sure we have a value
			if(input.length == 2)
			{
				try {
					newSector = new Vector2i(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim()));
					zoom = 0; // Switch back to sector view if they put in a sector, but not if the arrows are pushed
				}
				catch (Exception e) { }
			}
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(newSector));
		}
		else if(button == home) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(galaxy.getSector()));
		}
		else if(button == zoomOut) {
			if(zoom > 0)
				zoom -= 1;
		}
		else if(button == toggleGrid) {
			displayGrid = !displayGrid;
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
		try {
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
		try {
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
					// We've clicked on a solar system, so set the viewLocation to that system
					viewLocation = sys;
					// Change our zoom level to the system level
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
	 * Given the sector and subsector x & y, return the system in that sector at the subsector location
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
	 * Given the subsystem x & y, return the celestial at that subsystem location
	 * Returns null if there is no celestial at that location
	 * @param subX
	 * @param subY
	 * @return
	 */
	public Celestial getCelestialSubsystem(int subX, int subY)
	{
		List<Celestial> list = ((SolarSystem) viewLocation).getCelestials();
		for(int i = 0; i < list.size(); ++i)
		{
			Vector2i subsystem = Location.positionToSubsystem(list.get(i).getPosition());
			if(subsystem.getX() == subX && subsystem.getY() == subY)
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
		if(zoom == 0)
			return new Vector2i((mouseX - viewLeft) / viewSubsectors, (mouseY - viewTop) / viewSubsectors);
		else if(zoom == 1) // Gotta handle different size grids
			return new Vector2i((mouseX - viewLeft) / viewSubsystems, (mouseY - viewTop) / viewSubsystems);
		else
			return null;
	}
}
