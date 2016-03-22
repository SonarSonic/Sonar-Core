package sonar.core.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.SonarCore;
import sonar.core.common.block.SonarSidedBlock;
import sonar.core.network.PacketSonarSides;
import sonar.core.utils.ISonarSides;
import sonar.core.utils.MachineSide;

public abstract class TileEntitySidedInventory extends TileEntityInventory implements ISonarSides, ISidedInventory {

	public MachineSide[] sides = new MachineSide[]{MachineSide.INPUT,MachineSide.INPUT,MachineSide.INPUT,MachineSide.INPUT,MachineSide.INPUT,MachineSide.INPUT};
	public int[] input, output;

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagCompound sideNBT = nbt.getCompoundTag("sides");
		for (int i = 0; i < 6; i++) {
			MachineSide side = MachineSide.values()[sideNBT.getInteger("" + i)];
			if (side == null)
				sides[i] = MachineSide.INPUT;
			else
				sides[i] = side;
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound sideNBT = new NBTTagCompound();
		for (int i = 0; i < 6; i++) {
			sideNBT.setInteger("" + i, sides[i].ordinal());
		}
		nbt.setTag("sides", sideNBT);

	}

	public void sendPacket(int dimension, EnumFacing side) {		
		SonarCore.network.sendToAllAround(new PacketSonarSides(pos, side, sides[side.getIndex()]), new TargetPoint(dimension, pos.getX(), pos.getY(), pos.getZ(), 64));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return sides[side.getIndex()].isInput() ? input : output;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	@Override
	public boolean decrSide(EnumFacing side) {
		if (!this.getWorld().isRemote) {
			sides[side.getIndex()].decrease();
			sendPacket(this.worldObj.provider.getDimensionId(), side);
		}
		return false;
	}

	@Override
	public boolean incrSide(EnumFacing side) {
		if (!this.getWorld().isRemote) {			
			sides[side.getIndex()] = sides[side.getIndex()].increase();
			sendPacket(this.worldObj.provider.getDimensionId(), side);
		}
		return false;
	}

	@Override
	public boolean resetSides() {
		return false;
	}

	@Override
	public MachineSide getSideConfig(EnumFacing side) {
		return sides[side.getIndex()];
	}

	@Override
	public boolean setSide(EnumFacing side, MachineSide config) {
		sides[side.getIndex()] = config;
		this.worldObj.markBlockForUpdate(pos);
		return true;
	}

}