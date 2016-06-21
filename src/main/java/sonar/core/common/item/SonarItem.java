package sonar.core.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		if (isNew)
			list.add(EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + "New Feature!");
	}

}