package sonar.core.common.block;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.utils.ISpecialTooltip;

public abstract class SonarMachineBlock extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	protected SonarMachineBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
	}

	public abstract TileEntity createNewTileEntity(World world, int i);

	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam) {
		super.onBlockEventReceived(world, pos, state, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
	}

	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List list) {
	}

	public void standardInfo(ItemStack stack, EntityPlayer player, List list) {
	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return false;
	}

}
