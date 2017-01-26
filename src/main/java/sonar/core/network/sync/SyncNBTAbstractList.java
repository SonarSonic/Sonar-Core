package sonar.core.network.sync;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

/** for use with objects which implement INBTSyncable and have an Empty Constructor for instances */
public class SyncNBTAbstractList<T extends INBTSyncable> extends SyncPart {

	public ArrayList<T> objs = new ArrayList();
	public Class<T> type;

	public SyncNBTAbstractList(Class<T> type, int id) {
		super(id);
		this.type = type;
	}

	public SyncNBTAbstractList(Class<T> type, int id, int capacity) {
		super(id);
		this.type = type;
		objs = new ArrayList(capacity);
	}

	public ArrayList<T> getObjects() {
		return objs;
	}

	public void setObjects(ArrayList<T> list) {
		objs = list;
		markDirty();
	}

	public void addObject(T object) {
		if (!objs.contains(object)) {
			objs.add(object);
			markDirty();
		}
	}

	public void removeObject(T object) {
		if (objs.contains(object)) {
			objs.remove(object);
			markDirty();
		}
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
			ArrayList newObjs = new ArrayList();
			NBTTagList tagList = nbt.getTagList(getTagName(), Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				newObjs.add(NBTHelper.instanceNBTSyncable(this.type, tagList.getCompoundTagAt(i)));
			}
			objs = newObjs;
		} else if (nbt.getBoolean(getTagName() + "E")) {
			objs = new ArrayList();
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagList tagList = new NBTTagList();
		objs.forEach(obj -> {
			if (obj != null)
				tagList.appendTag(obj.writeData(new NBTTagCompound(), SyncType.SAVE));
		});
		if (!tagList.hasNoTags()) {
			nbt.setTag(getTagName(), tagList);
		} else {
			nbt.setBoolean(getTagName() + "E", true);
		}
		return nbt;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SyncNBTAbstractList) {
			return ((SyncNBTAbstractList) obj).getObjects().equals(this.objs);
		}
		return false;
	}
}
