package sonar.core.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public interface ISpecialTooltip {
	
    /**
     * add information to the tool tip if the stack has a TagCompound
     */
    void addSpecialToolTip(ItemStack stack, EntityPlayer player, List<String> list);

    void addSpecialToolTip(ItemStack stack, World world, List<String> list);

    /**
     * standard info always present on the tool tip
     */
    void standardInfo(ItemStack stack, EntityPlayer player, List<String> list);

    void standardInfo(ItemStack stack, World world, List<String> list);
}
