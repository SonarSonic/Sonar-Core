package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IGuiItem {

    int ID = -1;

    Object getGuiContainer(EntityPlayer player, ItemStack stack);

    Object getGuiScreen(EntityPlayer player, ItemStack stack);
}
