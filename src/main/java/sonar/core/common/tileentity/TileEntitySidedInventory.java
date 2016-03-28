package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.network.PacketSonarSides;
import sonar.core.utils.IMachineSides;
import sonar.core.utils.MachineSideConfig;
import sonar.core.utils.MachineSides;

public abstract class TileEntitySidedInventory extends TileEntityInventory implements IMachineSides, ISidedInventory {

	public MachineSides sides = new MachineSides(MachineSideConfig.INPUT, this, MachineSideConfig.NONE);
	public int[] input, output;

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		sides.readFromNBT(nbt);
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		sides.writeToNBT(nbt);
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return sides.getSideConfig(side.getOpposite()).isInput() ? input : output;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return sides.getSideConfig(direction.getOpposite()).isInput();
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return sides.getSideConfig(direction.getOpposite()).isOutput();
	}

	@Override
	public MachineSides getSideConfigs() {
		return sides;
	}
}