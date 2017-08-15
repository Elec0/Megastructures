package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

import java.util.ArrayList;
import java.util.List;

public class Planet extends Celestial
{
	// Need ore distributions, planet type (gas, solid, water, etc)

	private List<Satellite> satellites;

	public Planet() { super(); satellites = new ArrayList<>(); }
	public Planet(long seed) { super(seed); satellites = new ArrayList<>(); }


	@Override
	public void generate()
	{

	}

	public static Planet generateOverworld(long seed)
	{
		// If the planet we're generating is the one in which players go on
		Planet overworld = new Planet(seed);
		overworld.setName("Overworld");
		overworld.setRadius(6371);
		overworld.setMass(5.9e24); // kg

		Satellite moon = new Satellite(seed);
		moon.setName("Moon");
		moon.setRadius(1737);
		moon.setMass(7.34e22);
		moon.setPosition(new Vector2l(500, 0));
		overworld.addSatellite(moon);

		return overworld;
	}

	public void addSatellite(Satellite sat) { satellites.add(sat); }

	public List<Satellite> getSatellites() { return satellites; }
}
