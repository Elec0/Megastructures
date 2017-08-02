package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2l;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Galaxy extends Location
{
	private static final int SECTOR_DENSITY = 20; // Around how many solar systems should be in a sector

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
		// Not using linearly related seeds
		setSeed(world.getSeed() + (long)Math.pow(world.provider.getDimension(), 3));
	}

	/**
	 * First time galaxy generation. Should only be called once per dimension
	 * Will generate a certain amount of solar systems close to the overworld
	 */
	public void generate()
	{
		Random rand = new Random(getSeed());

		setName("Milky Way"); // Randomly generate this eventually

		// The galaxy's position is the 'center' of the galaxy, namely where the overworld system will be placed.
		setPosition(new Vector2l(rand.nextInt(), rand.nextInt()));
		// The galaxy's sector is the same as it's position
		setSector(positionToSector(getPosition()));

		// Generate the overworld solar system custom, since it needs specific planets
		SolarSystem overSystem = SolarSystem.generateOverSystem(rand.nextLong());
		overSystem.setPosition(getPosition());
		overSystem.setSector(positionToSector(overSystem.getPosition()));
		solarSystems.add(overSystem);

		int systems = rand.nextInt(SECTOR_DENSITY) + 10; // Generate at 10-30 solar systems
		for(int i = 0; i < systems; ++i)
		{
			// Since we set the seed specifically at the beginning of generation, all numbers will be generated
			//		the same, given the order. It's better than doing math on the actual seed.
			SolarSystem curSS = new SolarSystem(rand.nextLong());
			curSS.setSector(getSector()); // The generate method has to know what the sector is
			curSS.generate();

			solarSystems.add(curSS);
		}
	}

	public List<SolarSystem> getSolarSystems() { return solarSystems; }
	public void setSolarSystems(List<SolarSystem> solarSystems)
	{
		this.solarSystems = solarSystems;
	}

	public void addSolarSystem(SolarSystem system) { this.solarSystems.add(system); }

	@Override
	public String toString()
	{
		return getPosition() + ", SS: " + getSolarSystems().size();
	}

}
