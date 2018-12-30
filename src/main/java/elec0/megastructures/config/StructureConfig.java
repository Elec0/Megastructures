package elec0.megastructures.config;

import elec0.megastructures.Megastructures;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;

@Config.LangKey("megastructures.config.structures")
@Config(modid = Megastructures.MODID, category = "structures", name = Megastructures.MODID + "/structures")
public class StructureConfig
{
    @Config.Name("dysonsphere.materials")
    @Config.RequiresMcRestart
    @Config.Comment(value = "Stage, required oreDict materials and their quantities (stageNum:oreDict=qty)")
    public static HashMap<String, Integer> dsNeeded;

    @Config.Name("dysonsphere.max_stage")
    @Config.RequiresMcRestart
    @Config.Comment("Max stage of construction")
    public static int dsMaxStage;


    // Populate the hashmap with defaults
    // See https://github.com/Elec0/Megastructures/wiki/Material-Requirements-Calculations
    //  for calculations and reasoning
    static {
        dsMaxStage = 1;

        dsNeeded = new HashMap<>();
        // Stage 0 stuff
        dsNeeded.put("0:blockSand", (int)1e7);
        dsNeeded.put("0:blockGlass", (int)1e7);
        dsNeeded.put("0:ingotAluminum",  (int)1e8);
        dsNeeded.put("0:ingotCopper",  (int)1e6);

        // Stage 1 stuff
        dsNeeded.put("1:blockGlass", (int)2.544e20);
        dsNeeded.put("1:ingotIron", (int)2.544e18);
        dsNeeded.put("1:dustRedstone", (int)2.544e18);
        dsNeeded.put("1:ingotCopper", (int)1.272e18);

    }


}
