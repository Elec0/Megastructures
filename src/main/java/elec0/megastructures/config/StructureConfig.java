package elec0.megastructures.config;

import elec0.megastructures.Megastructures;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;

@Config(modid = Megastructures.MODID, category = "structureDysonSphere", name = Megastructures.MODID + "/structures")
@SuppressWarnings("unchecked")
public class StructureConfig
{
    @Config.Name("dysonSphereMaterials")
    @Config.RequiresMcRestart
    @Config.Comment(value = "Required oreDict materials and their quantities")
    public static HashMap<String, Integer> structureDSNeeded;

    static {
        int maxStage = 1;
        structureDSNeeded = new HashMap<>();
        // Populate the hashmap, with defaults, I guess?
        // Stage 0 stuff
        structureDSNeeded.put("ingotIron", 128);

        // Stage 1 stuff

    }



}
