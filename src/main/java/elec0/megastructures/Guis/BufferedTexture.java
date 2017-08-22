package elec0.megastructures.Guis;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BufferedTexture extends AbstractTexture
{

	public BufferedTexture()
	{

	}

	public void loadTexture(BufferedImage bufferedimage)
	{
		this.deleteGlTexture();

		boolean flag = false;
		boolean flag1 = false;

		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedimage, flag, flag1);
	}

	public void loadTexture(IResourceManager resMan) throws IOException
	{
		// Do nothing. Use SimpleTexture if you want this functionality.
	}
}
