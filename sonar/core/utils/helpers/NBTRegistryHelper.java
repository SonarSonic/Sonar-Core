package sonar.core.utils.helpers;

import io.netty.buffer.ByteBuf;
import sonar.core.utils.IBufManager;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTManager;
import sonar.core.utils.INBTObject;
import net.minecraft.nbt.NBTTagCompound;

public abstract class NBTRegistryHelper<T extends INBTObject> extends RegistryHelper implements INBTManager<T> {

	public T readFromNBT(NBTTagCompound tag) {
		return (T) NBTHelper.readNBTObject(tag, this);
	}

	public void writeToNBT(NBTTagCompound tag, T object) {
		NBTHelper.writeNBTObject(object, tag);
	}

	public abstract boolean equalTypes(T target, T current);

	public static abstract class Buf<T extends IBufObject> extends NBTRegistryHelper<T> implements IBufManager<T> {
		public T readFromBuf(ByteBuf buf) {
			return (T) NBTHelper.readBufObject(buf, this);
		}

		public void writeToBuf(ByteBuf buf, T object) {
			NBTHelper.writeBufObject(object, buf);
		}
	}

}