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
    static {
        dsMaxStage = 1;

        dsNeeded = new HashMap<>();
        // Stage 0 stuff
        dsNeeded.put("0:ingotIron", 128);
        // Stage 1 stuff
        dsNeeded.put("1:ingotIron", (int) 1e9);

    }


}
