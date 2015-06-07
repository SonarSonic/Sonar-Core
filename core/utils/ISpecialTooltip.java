package sonar.core.utils;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ISpecialTooltip {
	
	/**add information to the tool tip if the stack has a TagCompound*/
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List list);	

	/**standard info always present on the tool tip*/
	public void standardInfo(ItemStack stack, EntityPlayer player, List list);
}
