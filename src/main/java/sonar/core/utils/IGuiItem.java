package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IGuiItem {

	public static int ID = -1;

	public Object getGuiContainer(EntityPlayer player, ItemStack stack);

	public Object getGuiScreen(EntityPlayer player, ItemStack stack);
}
