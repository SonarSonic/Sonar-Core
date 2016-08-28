package sonar.core.network.sync;

import java.lang.reflect.ParameterizedType;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

/** for use with objects which implement INBTSyncable and have an Empty Constructor for instances */
public class SyncNBTAbstract<T extends INBTSyncable> extends SyncPart {

	public T obj;
	public Class<T> type;

	public SyncNBTAbstract(Class<T> type, int id) {
		super(id);
		this.type = type;
	}

	public T getObject() {
		return obj;
	}

	public void setObject(T object) {
		obj = object;
		setChanged(true);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName())) {
			if (obj != null)
				obj.readData(nbt.getCompoundTag(this.getTagName()), type);
			else {
				obj = NBTHelper.instanceNBTSyncable(this.type, nbt.getCompoundTag(this.getTagName()));
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (obj != null)
			nbt.setTag(getTagName(), obj.writeData(new NBTTagCompound(), type));
		return nbt;
	}

	@Nullable
	public T newInstance() {
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			SonarCore.logger.debug("FAILED TO CREATE NEW INSTANCE OF " + type.getSimpleName());
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SyncNBTAbstract) {
			return ((SyncNBTAbstract) obj).getObject() == this.obj;
		}
		return false;
	}

}
