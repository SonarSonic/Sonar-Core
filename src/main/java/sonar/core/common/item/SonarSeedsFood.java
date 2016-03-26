package sonar.core.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.helpers.FontHelper;
import sonar.core.integration.SonarLoader;

public class SonarSeedsFood extends ItemFood implements IPlantable {
	private Block cropBlock;
	private Block soilId;
	public int greenhouseTier;

	public SonarSeedsFood(int hunger, float saturation, Block crop, Block soil, int tier) {
		super(hunger, saturation, false);
		this.cropBlock = crop;
		this.soilId = soil;
		this.greenhouseTier = tier;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);

		if (SonarLoader.calculatorLoaded()) {
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
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz) {
		if (this.greenhouseTier == 0 || !SonarLoader.calculatorLoaded()) {
			if (side != EnumFacing.UP) {
				return false;
			} else if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(pos.offset(EnumFacing.UP), side, stack)) {
				if (world.getBlockState(pos).getBlock().canSustainPlant(world, pos, EnumFacing.UP, this) && world.isAirBlock(pos.offset(EnumFacing.UP))) {
					world.setBlockState(pos.offset(side), cropBlock.getDefaultState());
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

	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return cropBlock == Blocks.nether_wart ? EnumPlantType.Nether : EnumPlantType.Crop;
	}

	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return cropBlock.getDefaultState();
	}

	public boolean canTierUse(int tier) {
		if (tier >= this.greenhouseTier) {
			return true;
		}
		return false;
	}
}