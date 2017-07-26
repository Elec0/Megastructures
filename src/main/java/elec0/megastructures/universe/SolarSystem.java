package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		Planet overworld = new Planet(seed);
		overworld.isOverworld = true;
		overworld.generate();
		overworld.setPos(new Vector2l(152000000, 0));
		overSystem.addCelestial(overworld);

		Star sol = new Star(seed);
		// Don't need to set the pos, because sol is at the center of our solar system
		sol.setName("Sol");
		sol.setMass(1.988e30);
		sol.setRadius(696000);
		overSystem.addCelestial(sol);


		return overSystem;
	}


	public void generate()
	{
		Random rand = new Random(getSeed());
		setPosition(new Vector2l(rand.nextInt(), rand.nextInt()));
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
