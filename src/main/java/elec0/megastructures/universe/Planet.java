package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

import java.util.ArrayList;
import java.util.List;

public class Planet extends Celestial
{
	// Need ore distributions, planet type (gas, solid, water, etc)

	public boolean isOverworld = false;
	private List<Satellite> satellites;

	public Planet() { super(); satellites = new ArrayList<>(); }
	public Planet(long seed) { super(seed); satellites = new ArrayList<>(); }


	@Override
	public void generate()
	{
		if(isOverworld)
		{
			// If the planet we're generating is the one in which players go on
			setName("Overworld");
			setRadius(6371); // km
			setMass(5.9e24); // kg

			Satellite moon = new Satellite(getSeed());
			moon.setName("Moon");
			moon.setRadius(1737);
			moon.setMass(7.34e22);
			moon.setPos(new Vector2l(362000, 0));
		}
	}

	public void addSatellite(Satellite sat)
	{
		satellites.add(sat);
	}


	private class Satellite extends Planet
	{
		// Satellite position is position relative to the planet it's orbiting

		public Satellite() { super(); }
		public Satellite(long seed) { super(seed); }

		@Override
		public void generate()
		{

		}
	}
}
