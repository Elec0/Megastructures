package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

import java.util.ArrayList;
import java.util.List;

public class SolarSystem extends Location
{
	private List<Celestial> celestials;

	public SolarSystem()
	{
		super();
		celestials = new ArrayList<>();
	}
	public SolarSystem(long seed)
	{
		super(seed);
		celestials = new ArrayList<>();
	}


	public static SolarSystem generateOverSystem(long seed)
	{
		SolarSystem overSystem = new SolarSystem(seed);
		overSystem.setName("OverSystem");

		/*
			We are going to not assume Galaticraft's system is viewable in our system. If it was, we would have to deal with not being
				able to build things on the planets and such. We already have to deal with those dimensions accessing dimension 0's network.
		 */

		Star sol = new Star(seed);
		sol.setName("Sol");
		sol.setPosition(new Vector2l((Location.SUBSYSTEMS / 2) * Location.SUBSYSTEM_SIZE, (Location.SUBSYSTEMS / 2) * Location.SUBSYSTEM_SIZE)); // Put the sun in the middle of the system
		sol.setMass(1.988e30);
		sol.setRadius(696000);
		overSystem.addCelestial(sol);

		Planet overworld = Planet.generateOverworld(overSystem.getRand().nextLong());
		overworld.setPosition(new Vector2l(sol.getPosition().getX() - (4 * Location.SUBSYSTEM_SIZE), sol.getPosition().getY()));
		overSystem.addCelestial(overworld);

		return overSystem;
	}


	public void generate()
	{

	}

	public void addCelestial(Celestial celestial)
	{
		celestials.add(celestial);
	}

	public List<Celestial> getCelestials()
	{
		return celestials;
	}
	public void setCelestials(List<Celestial> celestials)
	{
		this.celestials = celestials;
	}
}
