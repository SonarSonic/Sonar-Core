package sonar.core.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.helpers.FontHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SonarSeedsFood extends ItemFood implements IPlantable {
	private Block blockCrop;
	private Block soilId;
	public int greenhouseTier;

	public SonarSeedsFood(int hunger, float saturation, Block crop, Block soil, int tier) {
		super(hunger, saturation, false);
		this.blockCrop = crop;
		this.soilId = soil;
		this.greenhouseTier = tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);

		String mode = FontHelper.translate("calculator.tools.calculator.greenhouse");
		switch (greenhouseTier) {
		case 0:
			break;
		case 1:
			list.add(FontHelper.translate("Planted with Basic Greenhouse or Higher"));
			break;
		case 2:
			list.add(FontHelper.translate("Planted with Advanced Greenhouse or Higher"));
			break;
		case 3:
			list.add(FontHelper.translate("Planted with Flawless Greenhouse"));
			break;
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par, float f1, float f2, float f3) {

		if (this.greenhouseTier == 0) {
			if (par != 1) {
				return false;
			} else if (player.canPlayerEdit(x, y, z, par, stack) && player.canPlayerEdit(x, y + 1, z, par, stack)) {
				if (world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, this) && world.isAirBlock(x, y + 1, z)) {
					world.setBlock(x, y + 1, z, this.blockCrop);
					--stack.stackSize;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Crop;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return blockCrop;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return 0;
	}

	public boolean canTierUse(int tier) {
		if (tier >= this.greenhouseTier) {
			return true;
		}
		return false;
	}
}