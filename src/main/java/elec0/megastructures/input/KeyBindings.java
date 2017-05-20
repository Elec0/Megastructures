package elec0.megastructures.input;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindings 
{
	public static KeyBinding powerKey1, powerKey2;
	
	public static void init()
	{
		powerKey1 = new KeyBinding("key.power1", Keyboard.KEY_X, "key.categories.simplypowers");
		powerKey2 = new KeyBinding("key.power2", Keyboard.KEY_C, "key.categories.simplypowers");
		ClientRegistry.registerKeyBinding(powerKey1);
		ClientRegistry.registerKeyBinding(powerKey2);
	}
}
