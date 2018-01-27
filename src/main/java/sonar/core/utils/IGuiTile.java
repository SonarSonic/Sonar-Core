package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;

/**use IFlexibleGui instead*/
@Deprecated 
public interface IGuiTile {

    int ID = -2;

    Object getGuiContainer(EntityPlayer player);

    Object getGuiScreen(EntityPlayer player);
}
