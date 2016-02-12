package sonar.core.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.NBTHelper;
import cpw.mods.fml.common.network.ByteBufUtils;

public class StoredEnergyStack {

	public double rfModifier;
	public double stored, capacity;
	public byte type;
	public final static byte storage = 0;
	public final static byte input = 1;
	public final static byte output = 2;
	public final static byte usage = 3;

	public StoredEnergyStack(byte type, double rfModifier, double stored, double capacity) {
		this.rfModifier = rfModifier;
		this.stored = stored;
		this.capacity = capacity;
		this.type = type;
	}

	public void add(StoredEnergyStack stack) {
		if (equalEnergyType(stack)) {
			stored += stack.stored;
			capacity += stack.capacity;
		}
	}

	public void remove(StoredEnergyStack stack) {
		if (equalEnergyType(stack)) {
			stored -= stack.stored;
			capacity -= stack.capacity;
		}
	}

	public boolean equalEnergyType(StoredEnergyStack stack) {
		return stack.type == type && stack.rfModifier == rfModifier;
	}

	public static StoredEnergyStack readFromNBT(NBTTagCompound tag) {
		return new StoredEnergyStack(tag.getByte("type"), tag.getDouble("rfModifier"), tag.getDouble("stored"), tag.getDouble("capacity"));
	}

	public static void writeToNBT(NBTTagCompound tag, StoredEnergyStack storedStack) {
		tag.setByte("type", storedStack.type);
		tag.setDouble("rfModifier", storedStack.rfModifier);
		tag.setDouble("stored", storedStack.stored);
		tag.setDouble("capacity", storedStack.capacity);
	}

	public static StoredEnergyStack readFromBuf(ByteBuf buf) {
		return new StoredEnergyStack(buf.readByte(), buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public static void writeToBuf(ByteBuf buf, StoredEnergyStack storedStack) {
		buf.writeByte(storedStack.type);
		buf.writeDouble(storedStack.rfModifier);
		buf.writeDouble(storedStack.stored);
		buf.writeDouble(storedStack.capacity);
	}

	public boolean equals(Object obj) {
		if (obj instanceof StoredEnergyStack) {
			StoredEnergyStack target = (StoredEnergyStack) obj;
			if (equalEnergyType(target) && this.stored == target.stored && this.capacity == target.capacity) {
				return true;
			}
		}
		return false;
	}
}
