package sonar.core.common.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.utils.ISpecialTooltip;

public abstract class SonarMachineBlock extends SonarBlock implements ITileEntityProvider, ISpecialTooltip {

	protected SonarMachineBlock(Material material) {
		super(material, true);
	}
	protected SonarMachineBlock(Material material, boolean bool) {
		super(material, bool);
	}
	protected SonarMachineBlock(Material material, boolean bool, boolean bool2) {
		super(material, bool, bool2);
	}
	public abstract TileEntity createNewTileEntity(World world, int i);
	
	
    public boolean onBlockEventReceived(World world, int x, int y, int z, int par, int par2)
    {
        super.onBlockEventReceived(world, x, y, z, par, par2);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null ? tileentity.receiveClientEvent(par, par2) : false;
    }
	public abstract static class FrontFacing extends SonarMachineBlock{

		protected FrontFacing(Material material) {
			super(material);
		}
		
	}

}
