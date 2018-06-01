package sonar.core.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpecialTooltip {
	
    /** add information to the tool tip if the stack has a TagCompoundm*/
    void addSpecialToolTip(ItemStack stack, World world, List<String> list, @Nullable NBTTagCompound tag);

}
