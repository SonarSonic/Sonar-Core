package sonar.core.helpers;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.IBufManager;
import sonar.core.api.nbt.IBufObject;
import sonar.core.api.nbt.INBTManager;
import sonar.core.api.nbt.INBTObject;

public abstract class NBTRegistryHelper<T extends INBTObject> extends RegistryHelper implements INBTManager<T> {

    @Override
	public T readFromNBT(NBTTagCompound tag) {
		return (T) NBTHelper.readNBTObject(tag, this);
	}

    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag, T object) {
		return NBTHelper.writeNBTObject(object, tag);
	}

	// public abstract boolean equalTypes(T target, T current);

	public static abstract class Buf<T extends IBufObject> extends NBTRegistryHelper<T> implements IBufManager<T> {
        @Override
		public T readFromBuf(ByteBuf buf) {
			return (T) NBTHelper.readBufObject(buf, this);
		}

        @Override
		public void writeToBuf(ByteBuf buf, T object) {
			NBTHelper.writeBufObject(object, buf);
		}
	}
}