package sonar.core.utils.helpers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import sonar.core.inventory.StoredItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.common.network.ByteBufUtils;

public class NBTHelper {

	public static void writeEnergyStorage(EnergyStorage storage, NBTTagCompound nbt) {
		NBTTagCompound energyTag = new NBTTagCompound();
		storage.writeToNBT(energyTag);
		nbt.setTag("energyStorage", energyTag);
	}

	public static void readEnergyStorage(EnergyStorage storage, NBTTagCompound nbt) {
		if (nbt.hasKey("energyStorage")) {
			storage.readFromNBT(nbt.getCompoundTag("energyStorage"));
		}
	}

	public static void writeFluidToBuf(FluidStack stack, ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, FluidRegistry.getFluidName(stack.getFluid()));
		buf.writeInt(stack.amount);
		if (stack.tag != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeTag(buf, stack.tag);
		} else {
			buf.writeBoolean(false);
		}
	}

	public static FluidStack readFluidFromBuf(ByteBuf buf) {
		String fluidName = ByteBufUtils.readUTF8String(buf);

		if (fluidName == null || FluidRegistry.getFluid(fluidName) == null) {
			return null;
		}
		FluidStack stack = new FluidStack(FluidRegistry.getFluid(fluidName), buf.readInt());

		if (buf.readBoolean()) {
			stack.tag = ByteBufUtils.readTag(buf);
		}
		return stack;
	}

	public static void writeTankInfo(FluidTankInfo tank, NBTTagCompound nbt) {
		tank.fluid.writeToNBT(nbt);
		nbt.setInteger("capacity", tank.capacity);
	}

	public static FluidTankInfo readTankInfo(NBTTagCompound nbt) {
		return new FluidTankInfo(FluidStack.loadFluidStackFromNBT(nbt), nbt.getInteger("capacity"));
	}

	public static enum SyncType {
		SAVE, SYNC, DROP, SPECIAL;

		public static byte getID(SyncType type) {
			switch (type) {
			case SYNC:
				return 1;
			case DROP:
				return 2;
			case SPECIAL:
				return 3;
			default:
				return 0;
			}
		}

		public static SyncType getType(int i) {
			switch (i) {
			case 1:
				return SYNC;
			case 2:
				return DROP;
			case 3:
				return SPECIAL;
			default:
				return SAVE;
			}

		}
	}

}
