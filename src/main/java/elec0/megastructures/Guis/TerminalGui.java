package elec0.megastructures.Guis;


import elec0.megastructures.Megastructures;
import elec0.megastructures.general.Vector2i;
import elec0.megastructures.network.PacketHandler;
import elec0.megastructures.network.PacketRequestDirector;
import elec0.megastructures.network.PacketRequestTerminalData;
import elec0.megastructures.structures.Structure;
import elec0.megastructures.universe.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class TerminalGui extends GuiScreen
{
	private GuiButton sectorLeft, sectorRight, sectorUp, sectorDown, zoomOut, home, sectorGo, toggleGrid;
	private GuiButton structCreate, structDelete;
	private GuiTextField sectorText;
	private Galaxy galaxy;
	private List<Structure> userStructures; // List of the current block's activator's owned structures
	private int zoom = 0; // 0 = galaxy overview, 1 = solar system overview, 2 = planet overview
	private int left, right, top, bottom;
	private int viewLeft, viewRight, viewTop, viewBottom, squareSize, viewSubsectors, viewSubsystems;
	private Vector2i viewSector;
	private Location viewLocation;
	private boolean displayGrid = true;

	private static final ResourceLocation background = new ResourceLocation(Megastructures.MODID, "textures/gui/terminal.png");

	private static final int w = 320, h = 150;
	private static final int PAD_HORIZ = 14, PAD_VERT = 14, BORDER_SIZE = 2, BORDER_VIEW = 3;

	private int structureBoxRight;


	public static NumberFormat decimalFormat = new DecimalFormat("0.####E0");

	// Update values
	private long tickCount;
	private static final int TICK_UPDATE = 5;

	// Button Text values
	private static final String BTN_LEFT 						= "◄";
	private static final String BTN_RIGHT 						= "►";
	private static final String BTN_UP 							= "▲";
	private static final String BTN_DOWN 						= "▼";
	private static final String BTN_ZOOM_OUT 					= "Zoom Out";
	private static final String BTN_HOME 						= "Home";
	private static final String BTN_GRID 						= "Toggle Grid";
	private static final String BTN_GO 							= "Go";
	private static final String BTN_CREATE 						= "Create";
	private static final String BTN_DELETE 						= "Delete";
	// End button text values
	private static int ID = 0;

	public TerminalGui() {}

	// <editor-fold desc="*** Draw Methods ***">

	// **************************
	// *** BEGIN DRAW METHODS ***
	// **************************
	/**
	 * drawScreen is called every frame, I believe. Or close enough to not matter.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.drawDefaultBackground();
		updateTickCount();
		calcBounds();
		drawBackground();

		if(galaxy != null)
		{
			drawView();
			handleMouse(mouseX, mouseY);
			String str = "";
			if(zoom == 0)
				str = galaxy.getName();
			else if(zoom == 1)
				str = viewLocation.getName() + ": " + Location.positionToSubsector(viewLocation.getPosition());
			else if(zoom == 2)
				str = "";

			fontRenderer.drawString(str, viewLeft - fontRenderer.getStringWidth(str) - 7, top + 2, 0x000000);
		}

		drawStructureInfo();

		sectorText.drawTextBox(); // This needs to be updated to account for input

		super.drawScreen(mouseX, mouseY, partialTicks); // Handles drawing things like buttons, which need to be over the background
	}

	/**
	 * Draw all things related to the structures, excluding the buttons
	 * TODO: Move this to a different GUI
	 */
	private void drawStructureInfo() {
		// Draw the background no matter what
		drawStructureBackground();

		// Draw the current progress of the structure(s), plus currently accepted materials and RF generation
		if(userStructures != null) {
			int base = top + BORDER_SIZE + 1;
			int baseLeft = left + BORDER_SIZE + 1;

			int wrapWidth = structureBoxRight - left - 1; // The 1 is for the 1 in baseLeft
			int linesDrawn = 0;

			// Currently just drawing things simply in a line, nothing special
			// We need to make/import a list or whatever for scrolling since structures are going to be of arbitrary limit in the future
			// Or, alternatively, make an entirely different GUI that only handles structures. I'm more inclined to do that for future-proofing
			for(int i = 0; i < userStructures.size(); ++i) {
				Structure s = userStructures.get(i);

				int line = linesDrawn * fontRenderer.FONT_HEIGHT + base;

				String curRF = decimalFormat.format(s.getEnergy());

				// If the name plus the border is longer than the wrap width then remove the border
				String name = String.format("-- %s --", s.getName());
				if(fontRenderer.getStringWidth(name) + 6 > wrapWidth)
					name = String.format("%s", s.getName());

				String stage = String.format("Stage %s of %s", s.getCurStage(), s.getMaxStage());

				String toDraw = String.format("%s\n%s %s%%\nRF: %s", name, stage, s.getProgress(s.getCurStage()), curRF);


				fontRenderer.drawSplitString(toDraw, baseLeft, line, wrapWidth, 0x000000);

				linesDrawn += fontRenderer.getStringWidth(toDraw) / wrapWidth + 2; // There's at least 1 line drawn
			}
		} else {
			// If there aren't any structures, display a text that says that
		}
	}

	private void drawStructureBackground() {
		drawRect(left + BORDER_SIZE, top + BORDER_SIZE, structureBoxRight, bottom - BORDER_SIZE, 0xFF737373); // Dark grey
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
				for(SolarSystem s : galaxy.getSectorList(viewSector))
				{
					Vector2i subsector = Location.positionToSubsector(s.getPosition());

					GL11.glPushMatrix();
					GL11.glTranslated(viewLeft + subsector.getX() * viewSubsectors, viewTop + subsector.getY() * viewSubsectors, 0);
					//fontRenderer.drawString("SS", 0, 0, 0xFF0000);
					GuiDrawing.drawSystem(this, s, viewSubsectors);
					GL11.glPopMatrix();
				}
				break;

			case 1: // System view
				for(Celestial c : ((SolarSystem) viewLocation).getCelestials())
				{
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
	 * Draw the rectangle that shows celestial information in the sector/solar system/planet view
	 * @param zoom
	 * @param subX
	 * @param subY
	 */
	private void drawInfo(int zoom, int subX, int subY) {
		// Keep same sizing as on system map (INFOs)
		// 4 squares width, 2 squares hieght
		int INFO_WIDTH = 4 * viewSubsectors, INFO_HEIGHT = 2 * viewSubsectors, PAD_LEFT = 2, PAD_TOP = 2;
		int left = viewLeft + subX * viewSubsystems + PAD_LEFT, top = viewTop + subY * viewSubsystems + PAD_TOP;

		Location loc = null;
		Vector2i.Vector2iInterface vecInt = null;

		if (zoom == 0) // Celestials
		{
			loc = getSystemSubsector(viewSector, subX, subY);
			// Functional programming, finally
			vecInt = Location::positionToSubsector;
		} else if (zoom == 1) // Solar Systems
		{
			loc = getCelestialSubsystem(subX, subY);
			vecInt = Location::positionToSubsystem;
		} else if (zoom == 2) // Planets
		{
		}

		if (loc != null) {
			Vector2i position = vecInt.position(loc.getPosition());
			drawRect(left, top, left + INFO_WIDTH + PAD_LEFT, top + INFO_HEIGHT + PAD_TOP, 0xBA9B9B9B);
			fontRenderer.drawSplitString(loc.getName() + " " + position, left + PAD_LEFT, top + PAD_TOP, INFO_WIDTH, 0x000000);
		}

	}

	// ************************
	// *** END DRAW METHODS ***
	// ************************
	// </editor-fold>

	//<editor-fold desc="*** One-Time Methods ***">

	// ******************************
	// *** BEGIN ONE-TIME METHODS ***
	// ******************************

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

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	@Override
	public void initGui()
	{
		calcBounds();

		int btnBorder = 6;
		int btnHeight = 20;


		// Arrows
		int btnSize = fontRenderer.getStringWidth("<-") + 4;
		sectorLeft = createButton(BTN_LEFT, viewLeft - (btnSize + btnBorder) * 2, height / 2, btnBorder + btnSize, btnHeight);
		sectorRight = createButton(BTN_RIGHT, viewLeft - (btnSize + btnBorder), height / 2, btnSize + btnBorder, btnHeight);
		sectorUp = createButton(BTN_UP, viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) - 20, btnSize + btnBorder, btnHeight);
		sectorDown = createButton(BTN_DOWN, viewLeft - (int)((btnSize + btnBorder) * 1.5), (height / 2) + 20, btnSize + btnBorder, btnHeight);

		// Zoom out
		btnSize = fontRenderer.getStringWidth(BTN_ZOOM_OUT);
		zoomOut = createButton(BTN_ZOOM_OUT, viewLeft - (btnSize + btnBorder), (height / 2) + btnHeight * 2, btnSize + btnBorder, btnHeight);

		// Home
		btnSize = fontRenderer.getStringWidth(BTN_HOME);
		home = createButton(BTN_HOME, viewLeft - (btnSize + btnBorder) - 7, (height / 2) - btnHeight * 2, btnSize + btnBorder, btnHeight);

		// Toggle Grid
		btnSize = fontRenderer.getStringWidth(BTN_GRID);
		toggleGrid = createButton(BTN_GRID, viewLeft - (btnSize + btnBorder), (height / 2) + btnHeight * 3, btnSize + btnBorder, btnHeight);

		// Go
		btnSize = fontRenderer.getStringWidth(BTN_GO);
		sectorGo = createButton(BTN_GO, viewLeft - (btnSize + btnBorder), viewTop + 10, btnSize + btnBorder, btnHeight);

		// Sector text box
		int txtSize = fontRenderer.getStringWidth("-100, -100");
		int x = viewLeft - (txtSize + (int)(btnBorder*1.5) + btnSize);
		sectorText = new GuiTextField(nextID(), fontRenderer, x, viewTop + 10, txtSize, 20);
		sectorText.setMaxStringLength(30);
		structureBoxRight = x - 1;
		//sectorText.setCanLoseFocus(true);

		// Create
		btnSize = fontRenderer.getStringWidth(BTN_CREATE);
		structCreate = createButton(BTN_CREATE, left + BORDER_SIZE, bottom - btnHeight, btnSize + btnBorder, btnHeight);

		// Delete
		int prevSize = btnSize + btnBorder;
		btnSize = fontRenderer.getStringWidth(BTN_DELETE);
		structDelete = createButton(BTN_DELETE, left + BORDER_SIZE + prevSize, bottom - btnHeight, btnSize + btnBorder, btnHeight);


		// To handle when the GUI is resized, everything is cleared so need to be re-initialized
		if(viewSector != null)
			sectorText.setText(viewSector.getX() + ", " + viewSector.getY());
		else if(galaxy != null)
			sectorText.setText(galaxy.getSector().getX() + ", " + galaxy.getSector().getY());

	}

	private GuiButton createButton(String buttonText, int x, int y, int widthIn, int btnHeight) {
		GuiButton btn = new GuiButton(nextID(), x, y, widthIn, btnHeight, buttonText);
		buttonList.add(btn);
		return btn;
	}

	// ***************************
	// ** END ONE-TIME METHODS ***
	// ***************************
	//</editor-fold>

	// <editor-fold desc="*** IO Methods ***">

	// ******************
	// *** IO Methods ***
	// ******************
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
			PacketHandler.INSTANCE.sendToServer(PacketRequestDirector.request(galaxy.getSector()));
		}
		else if(button == zoomOut) {
			if(zoom > 0)
				zoom -= 1;
		}
		else if(button == toggleGrid) {
			displayGrid = !displayGrid;
		}
		else if(button == structCreate) {
			PacketHandler.INSTANCE.sendToServer(PacketRequestDirector.request("create"));
		}
		else if(button == structDelete) {
			PacketHandler.INSTANCE.sendToServer(PacketRequestDirector.request("delete"));
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


	/**
	 * Called by PacketSendTerminalData when the packet is finished being received
	 */
	public void packetFinished(int opt)
	{
		// Have to wait till after the packet is received to set the text box
		if(opt == PacketRequestTerminalData.OPT_SECTOR || opt == PacketRequestTerminalData.OPT_BOTH) {
			String text = "";
			if (viewSector != null)
				text = viewSector.getX() + ", " + viewSector.getY();
			else if (galaxy != null) // If this isn't true we have problems
				text = galaxy.getSector().getX() + ", " + galaxy.getSector().getY();
			sectorText.setText(text);
		}
	}

	// **********************
	// *** END IO Methods ***
	// **********************
	// </editor-fold>

	// <editor-fold desc="*** Per-Tick Methods ***">

	// ************************
	// *** Per-Tick Methods ***
	// ************************


	@Override
	public void updateScreen()
	{
		super.updateScreen();
		sectorText.updateCursorCounter();
	}

	/**
	 * Handles the timer aspect of the gui for updating info from the server
	 */
	private void updateTickCount() {
		if(mc.world.getTotalWorldTime() >= tickCount + TICK_UPDATE) {
			requestUpdate();
			tickCount = mc.world.getTotalWorldTime();
		}
	}

	// ****************************
	// *** END Per-Tick Methods ***
	// ****************************
	// </editor-fold>


	/**
	 * Ask the server for an update. This is only for structure updating
	 */
	private void requestUpdate() {
		// Request the same place we're in
		if(viewSector != null && PacketHandler.INSTANCE != null)
			PacketHandler.INSTANCE.sendToServer(new PacketRequestTerminalData(true));
	}

	public void setGalaxy(Galaxy galaxy) { this.galaxy = galaxy; }
	public void setViewSector(Vector2i viewSector) { this.viewSector = viewSector; }
	public void setUserStructures(List<Structure> structureList) { this.userStructures = structureList; }
	private static int nextID() { return ID++; }

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}


	// <editor-fold desc="*** Utility Methods ***">

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

	//</editor-fold>
}
