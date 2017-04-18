package sonar.core.api.fluids;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.ISonarStack;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

public class StoredFluidStack implements ISonarStack<StoredFluidStack> {

	public FluidStack fluid;
	public long stored, capacity;

	public StoredFluidStack() {}

	public StoredFluidStack(FluidStack stack) {
		this.fluid = stack.copy();
		this.stored = stack.amount;
		this.capacity = stack.amount;
	}

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

	public StoredFluidStack copy() {
		return new StoredFluidStack(this.fluid, this.stored, this.capacity);
	}

	public StoredFluidStack setStackSize(long size) {
		this.stored = size;
		return this;
	}

	public StoredFluidStack setStackSize(StoredFluidStack stack) {
		this.stored = stack == null ? 0 : stack.getStackSize();
		return this;
	}

	public boolean equalStack(FluidStack stack) {
		if (this.fluid == null || stack == null){ //|| stack.amount == 0) {
			return false;
		}
		return this.fluid.isFluidEqual(stack);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		this.fluid = FluidStack.loadFluidStackFromNBT(nbt);
		this.stored = nbt.getLong("stored");
		this.capacity = nbt.getLong("capacity");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		fluid.writeToNBT(nbt);
		nbt.setLong("stored", stored);
		nbt.setLong("capacity", capacity);
		return nbt;
	}

	public static StoredFluidStack readFromBuf(ByteBuf buf) {
		return new StoredFluidStack(NBTHelper.readFluidFromBuf(buf), buf.readLong(), buf.readLong());
	}

	public static void writeToBuf(ByteBuf buf, StoredFluidStack storedStack) {
		NBTHelper.writeFluidToBuf(storedStack.fluid, buf);
		buf.writeLong(storedStack.stored);
		buf.writeLong(storedStack.capacity);
	}

	public boolean equals(Object obj) {
		if (obj instanceof StoredFluidStack) {
			StoredFluidStack target = (StoredFluidStack) obj;
			if (equalStack(target.fluid) && this.stored == target.stored && this.capacity == target.capacity) {
				return true;
			}
		}
		return false;
	}

	public FluidStack getFullStack() {
		FluidStack stack = fluid.copy();
		stack.amount = (int) Math.min(stored, Integer.MAX_VALUE);
		return stack;
	}

	@Override
	public StorageTypes getStorageType() {
		return StorageTypes.FLUIDS;
	}

	@Override
	public long getStackSize() {
		return stored;
	}
	
	public String toString() {
		if (fluid != null) {
			return this.stored +" mb " + "x " + this.fluid.getLocalizedName();
		} else {
			return super.toString() + " : NULL";
		}
	}
}
