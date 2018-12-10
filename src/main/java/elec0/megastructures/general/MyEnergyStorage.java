package elec0.megastructures.general;

import net.minecraftforge.energy.EnergyStorage;

/**
 * McJty's Tutorial Class
 * https://github.com/McJty/YouTubeModdingTutorial/blob/master/src/main/java/mcjty/mymod/tools/MyEnergyStorage.java
 * TODO: Figure out if I can just use EnergyStorage instead.
 */
public class MyEnergyStorage extends EnergyStorage {

	public MyEnergyStorage(int capacity, int maxReceive) {
		super(capacity, maxReceive, 0);
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void consumePower(int energy) {
		this.energy -= energy;
		if (this.energy < 0) {
			this.energy = 0;
		}
	}

	public void generatePower(int energy) {
		this.energy += energy;
		if (this.energy > capacity) {
			this.energy = capacity;
		}
	}

}