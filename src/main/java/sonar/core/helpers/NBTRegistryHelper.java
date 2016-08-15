package sonar.core.helpers;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.IBufManager;
import sonar.core.api.nbt.IBufObject;
import sonar.core.api.nbt.INBTManager;
import sonar.core.api.nbt.INBTObject;

public abstract class NBTRegistryHelper<T extends INBTObject> extends RegistryHelper implements INBTManager<T> {

	public T readFromNBT(NBTTagCompound tag) {
		return (T) NBTHelper.readNBTObject(tag, this);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag, T object) {
		return NBTHelper.writeNBTObject(object, tag);
	}

	// public abstract boolean equalTypes(T target, T current);

	public static abstract class Buf<T extends IBufObject> extends NBTRegistryHelper<T> implements IBufManager<T> {
		public T readFromBuf(ByteBuf buf) {
			return (T) NBTHelper.readBufObject(buf, this);
		}

		public void writeToBuf(ByteBuf buf, T object) {
			NBTHelper.writeBufObject(object, buf);
		}
	}

}