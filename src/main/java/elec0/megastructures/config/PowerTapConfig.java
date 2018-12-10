package elec0.megastructures.config;

import elec0.megastructures.Megastructures;
import net.minecraftforge.common.config.Config;

@Config(modid = Megastructures.MODID, category = "powertap")
public class PowerTapConfig
{
	@Config.Comment(value = "Maximum amount of power")
	public static int MAX_POWER = 100000;
}
