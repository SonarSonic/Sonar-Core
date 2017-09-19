package sonar.core.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class SonarItem extends Item {

	public boolean isNew;

	public SonarItem setNew() {
		isNew = true;
		return this;
	}

	public NBTTagCompound getTagCompound(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(getDefaultTag());
		}
		return stack.getTagCompound();
	}

	public NBTTagCompound getDefaultTag() {
		return new NBTTagCompound();
	}

	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
		if (isNew)
			list.add(TextFormatting.YELLOW + "" + TextFormatting.ITALIC + "New Feature!");
	}
}