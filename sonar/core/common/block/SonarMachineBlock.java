package sonar.core.common.block;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.utils.ISpecialTooltip;

public abstract class SonarMachineBlock extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	protected SonarMachineBlock(Material material) {
		super(material);
	}

	public abstract TileEntity createNewTileEntity(World world, int i);

	public boolean onBlockEventReceived(World world, int x, int y, int z, int par, int par2) {
		super.onBlockEventReceived(world, x, y, z, par, par2);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		return tileentity != null ? tileentity.receiveClientEvent(par, par2) : false;
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, EntityPlayer player, List list) {
	}

	@Override
	public void standardInfo(ItemStack stack, EntityPlayer player, List list) {
	}

	@Override
	public boolean dropStandard(World world, int x, int y, int z) {
		return false;
	}
	
}
