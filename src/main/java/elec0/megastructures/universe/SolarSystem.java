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

		Star sol = new Star(overSystem.getRand().nextLong());
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
		Vector2l center = new Vector2l((Location.SUBSYSTEMS / 2) * Location.SUBSYSTEM_SIZE, (Location.SUBSYSTEMS / 2) * Location.SUBSYSTEM_SIZE);
		int MAX_SUNS = 1, MAX_CELESTIALS = 20;

		// Generate gravatitional centers of the system
		int suns = getRand().nextInt(MAX_SUNS);
		//for(int i = 0; i < suns; ++i)
		{
			Star sun = new Star(getRand().nextLong());
			sun.setName("Sun");
			sun.setPosition(center);
			sun.generate();

			addCelestial(sun);
		}

		// Generate other planets
		int planets = getRand().nextInt(MAX_CELESTIALS);
		for(int i = 0; i < planets; ++i)
		{
			Planet p = new Planet(getRand().nextLong());
			p.setName("Planet " + (i+1));

			boolean canAdd;
			do
			{
				canAdd = true;
				int distCenter = getRand().nextInt(Location.SYSTEM_SIZE); // Distance from the center of the system the planet will be
				double percX = getRand().nextDouble(); // Percentage of distance from center is in the X direction
				double percY = 1 - percX; // Same as X but for Y, and is left over distance
				if(getRand().nextDouble() > 0.5) // Flip X/Y in half the cases
					percX *= -1;
				if(getRand().nextDouble() > 0.5)
					percY *= -1;
				p.setPosition(new Vector2l((long) (distCenter * percX), (long) (distCenter * percY)));

				// Check and make sure no other planet shares this location
				for(Celestial cel : getCelestials())
				{
					if(positionToSubsystem(cel.getPosition()).equals(positionToSubsystem(p.getPosition())))
					{
						// If it does, regenerate the planet until it doesn't
						canAdd = false;
						System.out.println("Celestial generation collision at " + p.getPosition());
					}
				}
				if(canAdd)
					p.generate();

			} while(!canAdd); // Only loop when canAdd is false

			addCelestial(p);
		}
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
