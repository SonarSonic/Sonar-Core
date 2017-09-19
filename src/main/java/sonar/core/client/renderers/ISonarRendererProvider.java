package sonar.core.client.renderers;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISonarRendererProvider {

	@SideOnly(Side.CLIENT)
    ISonarCustomRenderer getRenderer();
}
