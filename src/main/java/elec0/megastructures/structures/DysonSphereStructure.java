package elec0.megastructures.structures;

import com.google.gson.annotations.Since;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class DysonSphereStructure extends Structure
{
	private int distance;				// How far the sphere is from the sun in km
	private double distanceFalloff;		// How much energy is lost per DIST_SEGMENT km past min dist

	public static final int MIN_DISTANCE = (int)45e6; // 45 million km
	public static final int DIST_SEGMENT = 10000;
	public static final String NBT_DISTANCE = "distance";
	public static final String NBT_DISTANCE_FALLOFF = "distanceFalloff";


	public DysonSphereStructure(UUID player, String name, int distance) {
		super(player, name);
		setType(0);
		setProgress(100);

		setDistance(distance);
		// This value can change, this is just a test.
		// 0.0001 means a dyson sphere at pluto's orbit would lose 44.4% of the energy. Which is much too high, but whatever
		setDistanceFalloff(0.0001d); // 0.001%

		double maxEnergy = 3.85e27; // Watts put out by our sun
		maxEnergy /= 2; // Since we tick every half second, divide by 2
		maxEnergy /= 2.5; // Then divide by 2.5 to convert to RF
		maxEnergy *= (double)(getDistance() / DIST_SEGMENT) * getDistanceFalloff(); // Calculate energy falloff due to distance

		setMaxEnergyGen(maxEnergy); // RF/t

	}
	public DysonSphereStructure(NBTTagCompound nbtStructure) {
		super(nbtStructure); // This also calls deseralizeNBT
	}

//	@Override
//	public void generate() {
//		double prog = getProgress()/100d; // Convert this into a percentage
//		double energyToGen = prog * getMaxEnergyGen();
//		setEnergy(getEnergy() + energyToGen);
//	}

	public int getDistance(){return distance;}
	public void setDistance(int distance)
	{
		if(distance >= MIN_DISTANCE)
			this.distance = distance;
		else
			this.distance = MIN_DISTANCE;
	}
	public double getDistanceFalloff(){return distanceFalloff;}
	public void setDistanceFalloff(double distanceFalloff){this.distanceFalloff = distanceFalloff;}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		// This way we can have all the general information saved easily, and then can also include specific stuff

		nbt.setInteger(NBT_DISTANCE, distance);
		nbt.setDouble(NBT_DISTANCE_FALLOFF, distanceFalloff);
		return nbt;
	}

	/**
	 * Deseralize the saved structure
	 * @param nbt
	 */
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		super.deserializeNBT(nbt);
		this.distance = nbt.getInteger(NBT_DISTANCE);
		this.distanceFalloff = nbt.getDouble(NBT_DISTANCE_FALLOFF);

	}
}