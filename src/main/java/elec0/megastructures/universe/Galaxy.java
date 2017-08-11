package elec0.megastructures.universe;


import elec0.megastructures.general.Vector2i;
import elec0.megastructures.general.Vector2l;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Galaxy extends Location
{
	private static final double SECTOR_DENSITY = 0.1; // Probability of a solar system spawning in a subsector
	private static final int MAP_CAPACITY = 10000;

	private List<SolarSystem> solarSystems;
	private HashMap<Vector2i, List<SolarSystem>> sectorMap;

	public Galaxy(long seed)
	{
		super(seed);
		solarSystems = new ArrayList<>();
		sectorMap = new HashMap<>(MAP_CAPACITY);

		// This makes each dimension's seed unique, but still able to get the same solar systems out of the same minecraft seed
		setSeed(seed);
	}
	public Galaxy(World world)
	{
		super();
		solarSystems = new ArrayList<>();
		sectorMap = new HashMap<>(MAP_CAPACITY);
		// This makes each dimension's seed unique, but still able to get the same solar systems out of the same minecraft seed
		// Not using linearly related seeds
		setSeed(world.getSeed() + (long)Math.pow(world.provider.getDimension(), 3));
	}

	/**
	 * First time galaxy generation. Should only be called once per dimension
	 * Will generate the solar systems in the same sector as the overworld
	 * Use generate(sector) to generate specific sectors after initial generation
	 */
	public void generateInit()
	{
		setName("Milky Way"); // Randomly generate this eventually

		// The galaxy's position is the 'center' of the galaxy, namely where the overworld system will be placed.
		// Generate random position, turn that into a sector, then figure out which subsector to put the system in
		Vector2i tmpPos = positionToSector(new Vector2l(getRand().nextInt(), getRand().nextInt()));
		setPosition(new Vector2l(tmpPos.getX() + getRand().nextInt(SUBSECTORS) * SUBSECTOR_SIZE, tmpPos.getY() + getRand().nextInt(SUBSECTORS) * SUBSECTOR_SIZE));
		Vector2i skipSubSector = positionToSubsector(getPosition()); // Skip this subsector on generation or problems will be had

		// The galaxy's sector is the same as it's position
		setSector(positionToSector(getPosition()));

		// Generate the overworld solar system custom, since it needs specific planets
		SolarSystem overSystem = SolarSystem.generateOverSystem(getRand().nextLong());
		overSystem.setPosition(getPosition());
		addSolarSystem(overSystem);

		// Loop through each square subsector
		// subsector 0,0 is top left of the sector
		for(int i = 0; i < SUBSECTORS; ++i)
		{
			for(int j = 0; j < SUBSECTORS; ++j)
			{
				if(skipSubSector.getX() == j && skipSubSector.getY() == i) // Skip the overworld subsector in generation since it's already done
					continue;

				// Determine if a solar system should be placed in this subsector
				if(getRand().nextDouble() < SECTOR_DENSITY)
				{
					// Since we set the seed specifically at the beginning of generation, all numbers will be generated
					//		the same, given the order. It's better than doing math on the actual seed.
					SolarSystem curSS = new SolarSystem(getRand().nextLong());
					Vector2l curPos = sectorToPositon(getSector());
					// Set the system's position to the subsector position. The positions are not random anymore, but if a system is spawned
					// 	in a certain position is now random.

					// The ternaries are for making sure we aren't adding to a negative number. We must treat the top left of the sector as 0,0.
					curSS.setPosition(new Vector2l(curPos.getX() + (curPos.getX() > 0 ? j * SUBSECTOR_SIZE : j * SUBSECTOR_SIZE * -1), curPos.getY() + (curPos.getY() > 0 ? i * SUBSECTOR_SIZE : i * SUBSECTOR_SIZE * -1)));
					curSS.generate();

					addSolarSystem(curSS);
				}
			}
		}

	}

	public void generate(Vector2i sectorGenerate)
	{
		// Loop through each square subsector
		// subsector 0,0 is top left of the sector
		for(int i = 0; i < SUBSECTORS; ++i)
		{
			for(int j = 0; j < SUBSECTORS; ++j)
			{
				// Determine if a solar system should be placed in this subsector
				if(getRand().nextDouble() < SECTOR_DENSITY)
				{
					// Since we set the seed specifically at the beginning of generation, all numbers will be generated
					//		the same, given the order. It's better than doing math on the actual seed.
					SolarSystem curSS = new SolarSystem(getRand().nextLong());
					Vector2l curPos = sectorToPositon(sectorGenerate);
					// Set the system's position to the subsector position. The positions are not random anymore, but if a system is spawned
					// 	in a certain position is now random.

					// The ternaries are for making sure we aren't adding to a negative number. We must treat the top left of the sector as 0,0.
					curSS.setPosition(new Vector2l(curPos.getX() + (curPos.getX() > 0 ? j * SUBSECTOR_SIZE : j * SUBSECTOR_SIZE * -1), curPos.getY() + (curPos.getY() > 0 ? i * SUBSECTOR_SIZE : i * SUBSECTOR_SIZE * -1)));
					curSS.generate();

					addSolarSystem(curSS);
				}
			}
		}
	}

	public List<SolarSystem> getSolarSystems() { return solarSystems; }
	public List<SolarSystem> getSectorList(Vector2i sector) { return sectorMap.get(sector); }
	public HashMap<Vector2i, List<SolarSystem>> getSectors() { return sectorMap; }

	/**
	 * Adds the given solar system to the galaxy's storage.
	 * Solar system must have a sector defined.
	 * @param system
	 */
	public void addSolarSystem(SolarSystem system)
	{
		List<SolarSystem> sector;
		if(sectorMap.containsKey(system.getSector()))
		 	sector = sectorMap.get(system.getSector());
		else
			sector = new ArrayList<>();
		sector.add(system);
		// I don't think we need to put this back in, if it wasn't null to begin with, but just in case
		sectorMap.put(system.getSector(), sector);

		// We'll keep this for now
		this.solarSystems.add(system);
	}

	@Override
	public String toString()
	{
		return getPosition() + ", Sectors: " + getSectors().size() + ", SS: " + getSolarSystems().size();
	}

}
