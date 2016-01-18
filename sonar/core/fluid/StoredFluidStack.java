package sonar.core.fluid;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.NBTHelper;
import cpw.mods.fml.common.network.ByteBufUtils;

public class StoredFluidStack {

	public FluidStack fluid;
	public long stored, capacity;

	public StoredFluidStack(FluidStack stack, long capacity) {
		this.fluid = stack.copy();
		this.stored = stack.amount;
		this.capacity = capacity;
	}

	public StoredFluidStack(FluidStack stack, long stored, long capacity) {
		this.fluid = stack.copy();
		this.stored = stored;
		this.capacity = capacity;
	}

	public void add(StoredFluidStack stack) {
		if (equalStack(stack.fluid)) {
			stored += stack.stored;
			capacity += stack.capacity;
		}
	}

	public void remove(StoredFluidStack stack) {
		if (equalStack(stack.fluid)) {
			stored -= stack.stored;
			capacity -= stack.capacity;
		}
	}

	public boolean equalStack(FluidStack stack) {
		if (this.fluid == null || stack == null || stack.amount == 0) {
			return false;
		}
		return this.fluid.isFluidEqual(stack);
	}

	public static StoredFluidStack readFromNBT(NBTTagCompound tag) {
		return new StoredFluidStack(FluidStack.loadFluidStackFromNBT(tag), tag.getLong("stored"), tag.getLong("capacity"));
	}

	public static void writeToNBT(NBTTagCompound tag, StoredFluidStack storedStack) {
		storedStack.fluid.writeToNBT(tag);
		tag.setLong("stored", storedStack.stored);
		tag.setLong("capacity", storedStack.capacity);
	}

	public static StoredFluidStack readFromBuf(ByteBuf buf) {
		return new StoredFluidStack(NBTHelper.readFluidFromBuf(buf), buf.readLong(), buf.readLong());
	}

	public static void writeToBuf(ByteBuf buf, StoredFluidStack storedStack) {
		NBTHelper.writeFluidToBuf(storedStack.fluid, buf);
		buf.writeLong(storedStack.stored);
		buf.writeLong(storedStack.capacity);
	}

}
