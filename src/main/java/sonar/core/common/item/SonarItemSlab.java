package sonar.core.common.item;

import sonar.core.common.block.SonarSlab;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SonarItemSlab extends ItemBlock {
	private final SonarSlab singleSlab;
	private final SonarSlab doubleSlab;

	public SonarItemSlab(Block block, SonarSlab singleSlab, SonarSlab doubleSlab) {
		super(block);
		this.singleSlab = singleSlab;
		this.doubleSlab = doubleSlab;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int damage) {
		return damage;
	}

	public String getUnlocalizedName(ItemStack stack) {
		return this.singleSlab.getUnlocalizedName();
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (stack.stackSize == 0) {
			return false;
		} else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack)) {
			return false;
		} else {
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (iblockstate.getBlock() == this.singleSlab) {
				BlockSlab.EnumBlockHalf blockslab$enumblockhalf = (BlockSlab.EnumBlockHalf) iblockstate.getValue(BlockSlab.HALF);

				if ((side == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP)) {

					if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iblockstate)) && worldIn.setBlockState(pos, iblockstate, 3)) {
						worldIn.playSoundEffect((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
						--stack.stackSize;
					}

					return true;
				}
			}

			return this.tryPlace(stack, worldIn, pos.offset(side)) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		BlockPos blockpos = pos;
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (iblockstate.getBlock() == this.singleSlab) {
			boolean flag = iblockstate.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
			if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag)) {
				return true;
			}
		}

		pos = pos.offset(side);
		IBlockState iblockstate1 = worldIn.getBlockState(pos);
		return iblockstate1.getBlock() == this.singleSlab ? true : super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
	}

	private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (iblockstate.getBlock() == this.singleSlab) {
			if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, iblockstate)) && worldIn.setBlockState(pos, iblockstate, 3)) {
				worldIn.playSoundEffect((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
				--stack.stackSize;
			}

			return true;

		}

		return false;
	}
}