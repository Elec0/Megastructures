package elec0.megastructures.config;

import java.util.HashMap;
import java.util.Map;

public class MegastructuresConfig
{

    /**
     * Assume hashmap has values of 'i:oreDictValue', num
     * Process these into an array of hashmaps with i corresponding to the index
     * @param map
     * @param maxStage
     * @return
     */
    @SuppressWarnings("unchecked")
    public static HashMap[] processCompressedHashMap(HashMap<String, Integer> map, int maxStage) {
        HashMap[] result = new HashMap[maxStage + 1];

        // Ensure all the results are initialized
        for(int i = 0; i < result.length; ++i) {
            result[i] = new HashMap<String, Integer>();
        }


        // Loop through and process each of the key value pairs
        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            String value = entry.getKey();
            int count = entry.getValue();

            // Verify proper inputs, since this can come from the user and might be literally anything
            if(!value.contains(":")) {
                System.out.println(String.format("MegastructuresConfig.processCompressedHashMap(): invalid value: %s, must contain ':'", value));
                continue;
            }
            if(count <= 0) {
                System.out.println(String.format("MegastructuresConfig.processCompressedHashMap(): invalid count: %s, must be > 0", count));
                continue;
            }

            // Find the stage given for the current kvp
            String[] parse = value.split(":");

            try {
                int stage = Integer.parseInt(parse[0]);
                result[stage].put(parse[1], count);

            }
            catch(NumberFormatException e) {
                System.out.println(String.format("MegastructuresConfig.processCompressedHashMap(): invalid stage: %s", parse[0]));
            }
            catch(ArrayIndexOutOfBoundsException e) {
                System.out.println(String.format("MegastructuresConfig.processCompressedHashMap(): array out of bounds: %s", value));
                e.printStackTrace();
            }
        }

        return result;
    }
}
