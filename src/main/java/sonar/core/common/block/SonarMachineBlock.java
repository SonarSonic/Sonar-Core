package sonar.core.common.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.utils.ISpecialTooltip;

import java.util.List;

public abstract class SonarMachineBlock extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	protected SonarMachineBlock(Material material, boolean orientation, boolean wrenchable) {
		super(material, orientation, wrenchable);
	}

    //public abstract TileEntity createNewTileEntity(World world, int i);
	/*
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param){
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(id, param) : false;
	}
	*/
    @Override
    public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List<String> list) {
	}

    @Override
    public void standardInfo(ItemStack stack, EntityPlayer player, List<String> list) {
    }

    @Override
    public void addSpecialToolTip(ItemStack itemStack, World world, List<String> list) {

    }

    @Override
    public void standardInfo(ItemStack itemStack, World world, List<String> list) {

	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return false;
	}
}
