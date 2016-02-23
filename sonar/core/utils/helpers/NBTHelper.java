package sonar.core.utils.helpers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sonar.core.SonarCore;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.common.network.ByteBufUtils;

public class NBTHelper {

	public static void readSyncedNBTObjectList(String tagName, NBTTagCompound tag, NBTRegistryHelper<? extends INBTObject> helper, List objectList) {
		if (tag.hasKey(tagName + "null")) {
			objectList = Collections.EMPTY_LIST;
			return;
		} else if (tag.hasKey(tagName)) {
			NBTTagList list = tag.getTagList(tagName, 10);
			if (objectList == null) {
				objectList = new ArrayList();
			}

			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int slot = compound.getInteger("Slot");
				boolean set = slot < objectList.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set) {
						objectList.set(slot, helper.readFromNBT(compound));
					} else {
						objectList.add(slot, helper.readFromNBT(compound));
					}
					break;
				case 1:
					long stored = compound.getLong("Stored");
					if (stored != 0) {
						objectList.set(slot, helper.readFromNBT(compound));
					} else {
						objectList.set(slot, null);

					}
					break;
				case 2:
					objectList.set(slot, null);
					break;
				}
			}
		}
	}

	public static void writeSyncedNBTObjectList(String tagName, NBTTagCompound tag, NBTRegistryHelper helper, List objectList, List lastList) {
		if (objectList == null) {
			objectList = new ArrayList();
		}
		if (lastList == null) {
			lastList = new ArrayList();
		}
		NBTTagList list = new NBTTagList();
		int size = Math.max(objectList.size(), lastList.size());
		for (int i = 0; i < size; ++i) {
			INBTObject current = null;
			INBTObject last = null;
			if (i < objectList.size()) {
				current = (INBTObject) objectList.get(i);
			}
			if (i < lastList.size()) {
				last = (INBTObject) lastList.get(i);
			}
			NBTTagCompound compound = new NBTTagCompound();
			if (current != null) {
				if (last != null) {
					if (!helper.areTypesEqual(current, last)) {
						compound.setByte("f", (byte) 0);
						lastList.set(i, current);
						helper.writeToNBT(compound, (INBTObject) objectList.get(i));
					} else {

					}
				} else {
					compound.setByte("f", (byte) 0);
					lastList.add(i, current);
					helper.writeToNBT(compound, (INBTObject) objectList.get(i));
				}
			} else if (last != null) {
				lastList.set(i, null);
				compound.setByte("f", (byte) 2);
			}
			if (!compound.hasNoTags()) {
				compound.setInteger("Slot", i);
				list.appendTag(compound);
			}

		}
		if (list.tagCount() != 0) {
			tag.setTag(tagName, list);

		}
	}

	public static List<? extends INBTObject> readNBTObjectList(String tagName, NBTTagCompound tag, RegistryHelper<? extends INBTObject> helper) {
		List<INBTObject> objects = new ArrayList();
		if (tag.hasKey(tagName)) {
			NBTTagList list = tag.getTagList(tagName, 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				objects.add(readNBTObject(compound, helper));
			}
		}
		return objects;
	}

	public static void writeNBTObjectList(String tagName, NBTTagCompound tag, List<? extends INBTObject> objects) {
		if (objects == null || objects.isEmpty() || objects.size() == 0) {
			return;
		}
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				writeNBTObject(objects.get(i), compound);
				list.appendTag(compound);
			}
		}

		tag.setTag(tagName, list);
	}

	public static INBTObject readNBTObject(NBTTagCompound tag, RegistryHelper<? extends INBTObject> helper) {
		if (tag.hasKey("type")) {
			String type = tag.getString("type");
			if (type.equals("NULLED")) {
				return null;
			}
			if (helper.getRegisteredObject(type) == null) {
				SonarCore.logger.warn("NBT ERROR: " + "Unregistered " + helper.registeryType() + ": " + type);
				return null;
			}
			INBTObject filter = (INBTObject) helper.getRegisteredObject(type).instance();
			filter.readFromNBT(tag);
			return filter;
		} else {
			return null;
		}
	}

	public static void writeNBTObject(INBTObject object, NBTTagCompound tag) {
		if (object != null) {
			tag.setString("type", object.getName());
			object.writeToNBT(tag);
		} else {
			tag.setString("type", "NULLED");
		}
	}

	public static IBufObject readBufObject(ByteBuf buf, RegistryHelper<? extends IBufObject> helper) {
		if (buf.readBoolean()) {
			String type = ByteBufUtils.readUTF8String(buf);
			if (helper.getRegisteredObject(type) == null) {
				SonarCore.logger.warn("BYTE BUF: " + "Unregistered " + helper.registeryType() + ": " + type);
				return null;
			}
			IBufObject info = (IBufObject) helper.getRegisteredObject(type).instance();
			info.readFromBuf(buf);
			return info;

		} else {
			return null;
		}
	}

	public static void writeBufObject(IBufObject object, ByteBuf buf) {
		if (object != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, object.getName());
			object.writeToBuf(buf);
		} else {
			buf.writeBoolean(false);
		}
	}

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
		SAVE, SYNC, DROP, SPECIAL, PACKET;

		public static byte getID(SyncType type) {
			switch (type) {
			case SYNC:
				return 1;
			case DROP:
				return 2;
			case SPECIAL:
				return 3;
			case PACKET:
				return 4;
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
			case 4:
				return PACKET;
			default:
				return SAVE;
			}

		}
	}

	public static void writeNBTBase(NBTTagCompound nbt, int type, Object object, String tagName) {
		if(object==null){
			SonarCore.logger.error("NBT ERROR: Can't write NULL");
			return;
		}
		if(tagName==null){
			SonarCore.logger.error("NBT ERROR: Can't write with a no TAG NAME");
			return;
		}
		switch (type) {
		case NBT.TAG_END:
			nbt.setBoolean(tagName, (Boolean) object);
			return;
		case NBT.TAG_BYTE:
			nbt.setByte(tagName, (Byte) object);
			return;
		case NBT.TAG_SHORT:
			nbt.setShort(tagName, (Short) object);
			return;
		case NBT.TAG_INT:
			nbt.setInteger(tagName, (Integer) object);
			return;
		case NBT.TAG_LONG:
			nbt.setLong(tagName, (Long) object);
			return;
		case NBT.TAG_FLOAT:
			nbt.setFloat(tagName, (Float) object);
			return;
		case NBT.TAG_DOUBLE:
			nbt.setDouble(tagName, (Double) object);
			return;
		case NBT.TAG_BYTE_ARRAY:
			nbt.setByteArray(tagName, (byte[]) object);
			return;
		case NBT.TAG_STRING:
			nbt.setString(tagName, (String) object);
			return;
		case NBT.TAG_LIST:
			nbt.setTag(tagName, (NBTBase) object);
			return;
		case NBT.TAG_COMPOUND:
			nbt.setTag(tagName, (NBTTagCompound) object);
			return;
		case NBT.TAG_INT_ARRAY:
			nbt.setIntArray(tagName, (int[]) object);
			return;
		}
	}

	public static Object readNBTBase(NBTTagCompound nbt, int type, String tagName) {
		switch (type) {
		case NBT.TAG_END:
			return nbt.getBoolean(tagName);
		case NBT.TAG_BYTE:
			return nbt.getByte(tagName);
		case NBT.TAG_SHORT:
			return nbt.getShort(tagName);
		case NBT.TAG_INT:
			return nbt.getInteger(tagName);
		case NBT.TAG_LONG:
			return nbt.getLong(tagName);
		case NBT.TAG_FLOAT:
			return nbt.getFloat(tagName);
		case NBT.TAG_DOUBLE:
			return nbt.getDouble(tagName);
		case NBT.TAG_BYTE_ARRAY:
			return nbt.getByteArray(tagName);
		case NBT.TAG_STRING:
			return nbt.getString(tagName);
		case NBT.TAG_COMPOUND:
			return nbt.getTag(tagName);
		case NBT.TAG_INT_ARRAY:
			return nbt.getIntArray(tagName);
		default:
			return null;
		}
	}

	public static void writeBufBase(ByteBuf buf, int type, Object object, String tagName) {
		switch (type) {
		case NBT.TAG_END:
			buf.writeBoolean((Boolean) object);
			return;
		case NBT.TAG_BYTE:
			buf.writeByte((Byte) object);
			return;
		case NBT.TAG_SHORT:
			buf.writeShort((Short) object);
			return;
		case NBT.TAG_INT:
			buf.writeInt((Integer) object);
			return;
		case NBT.TAG_LONG:
			buf.writeLong((Long) object);
			return;
		case NBT.TAG_FLOAT:
			buf.writeFloat((Float) object);
			return;
		case NBT.TAG_DOUBLE:
			buf.writeDouble((Double) object);
			return;
		case NBT.TAG_BYTE_ARRAY:
			Byte[] byteArray = (Byte[]) object;
			buf.writeInt(byteArray.length);
			for (int i = 0; i < byteArray.length; i++) {
				buf.writeByte(byteArray[i]);
			}
			return;
		case NBT.TAG_STRING:
			ByteBufUtils.writeUTF8String(buf, (String) object);
			return;
		case NBT.TAG_LIST:
			ByteBufUtils.writeTag(buf, (NBTTagCompound) object);
			return;
		case NBT.TAG_COMPOUND:
			ByteBufUtils.writeTag(buf, (NBTTagCompound) object);
			return;
		case NBT.TAG_INT_ARRAY:
			byte[] intArray = (byte[]) object;
			buf.writeInt(intArray.length);
			for (int i = 0; i < intArray.length; i++) {
				buf.writeInt(intArray[i]);
			}
			return;
		}
	}

	public static Object readBufBase(ByteBuf buf, int type, String tagName) {
		switch (type) {
		case NBT.TAG_END:
			return buf.readBoolean();
		case NBT.TAG_BYTE:
			return buf.readByte();
		case NBT.TAG_SHORT:
			return buf.readShort();
		case NBT.TAG_INT:
			return buf.readInt();
		case NBT.TAG_LONG:
			return buf.readLong();
		case NBT.TAG_FLOAT:
			return buf.readFloat();
		case NBT.TAG_DOUBLE:
			return buf.readDouble();
		case NBT.TAG_BYTE_ARRAY:
			int byteArraySize = buf.readInt();
			byte[] byteArray = new byte[byteArraySize];
			for (int i = 0; i < byteArray.length; i++) {
				byteArray[i] = buf.readByte();
			}
			return byteArray;
		case NBT.TAG_STRING:
			return ByteBufUtils.readUTF8String(buf);
		case NBT.TAG_LIST:
		case NBT.TAG_COMPOUND:
			return ByteBufUtils.readTag(buf);
		case NBT.TAG_INT_ARRAY:
			int intArraySize = buf.readInt();
			int[] intArray = new int[intArraySize];
			for (int i = 0; i < intArray.length; i++) {
				intArray[i] = buf.readInt();
			}
			return intArray;
		default:
			return null;
		}
	}
}
