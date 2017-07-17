package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Galaxy extends Location
{
	private List<SolarSystem> solarSystems;

	public Galaxy(long seed)
	{
		super(seed);
		solarSystems = new ArrayList<>();
		// This makes each dimension's seed unique, but still able to get the same solar systems out of the same minecraft seed
		setSeed(seed);
	}
	public Galaxy(World world)
	{
		super();
		solarSystems = new ArrayList<>();
		// This makes each dimension's seed unique, but still able to get the same solar systems out of the same minecraft seed
		setSeed(world.getSeed() + world.provider.getDimension());
	}

	/**
	 * First time galaxy generation. Should only be called once per dimension
	 * Will generate a certain amount of solar systems close to the overworld
	 */
	public void generate()
	{
		Random rand = new Random(getSeed());

		// The galaxy's galaxyPos is the 'center' of the galaxy, namely where the overworld system will be placed.
		setGalaxyPos(new Vector2l(rand.nextInt(), rand.nextInt()));

		// Generate the overworld solar system custom, since it needs specific planets
		SolarSystem overSystem = SolarSystem.generateOverSystem(getSeed() - 1);
		overSystem.setSolarSystemPos(getGalaxyPos());
		solarSystems.add(overSystem);

		int systems = rand.nextInt(20) + 20; // Generate at 20-40 solar systems
		for(int i = 0; i < systems; ++i)
		{
			// I'm changing the seeds for each solar system since it is possible they could start to look similar to the galaxies
			// 		depending on how I make it
			SolarSystem curSS = new SolarSystem(getSeed() + i);


			//solarSystems.add(curSS);
		}
	}

	public List<SolarSystem> getSolarSystems()
	{
		return solarSystems;
	}
	public void setSolarSystems(List<SolarSystem> solarSystems)
	{
		this.solarSystems = solarSystems;
	}

	@Override
	public String toString()
	{
		return getGalaxyPos() + ", SS: " + getSolarSystems().size();
	}

}
