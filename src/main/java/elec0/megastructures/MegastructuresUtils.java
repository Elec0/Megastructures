package elec0.megastructures;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MegastructuresUtils 
{
	// Note: commas are not allowed in names or descriptions
	/**
	 * @param input
	 * @return
	 */
	public static HashMap<String, Integer> readHashMapFromString(String input) {
		HashMap<String, Integer> result = new HashMap<>();

		String[] pairs = input.split(";");
		for(String s : pairs) {
			String[] kvp = s.split(",");
			int count = Integer.parseInt(kvp[1]);
			result.put(kvp[0], count);
		}
		return result;
	}

	/**
	 * @param input
	 * @return
	 */
	public static String createStringFromHashMap(HashMap<String, Integer> input) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Integer> entry : input.entrySet())
		{
			String oreName = entry.getKey();
			int count = entry.getValue();
			sb = sb.append(oreName).append(",").append(count).append(";");
		}
		// Don't try to substring if it will error
		if(sb.length() >= 1)
			return sb.substring(0, sb.length() - 1);
		return sb.toString();
	}
}
