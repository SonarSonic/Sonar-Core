package sonar.core.network.sync;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;

/**for use when the type of object stored is subject to change, this can only be used with primitives*/
public class SyncUnidentifiedObject extends SyncPart {

	public Object obj = null;
	public ObjectType objectType = null;

	public SyncUnidentifiedObject(int id) {
		super(id);
	}

	public void set(Object obj, ObjectType objectType) {
		if (obj != null && (this.obj == null || !obj.equals(this.obj))) {
			this.obj = obj;
			this.objectType = objectType;
			this.markDirty();
		}
	}

	@Nullable
	public Object get() {
		return obj;
	}

	public boolean hasObject() {
		return obj != null && objectType != null;
	}

	public boolean hasObject(NBTTagCompound tag) {
		return tag.hasKey(getTagName() + "obj");
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
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (hasObject()) {
			nbt.setInteger(getTagName() + "type", objectType.ordinal());
			NBTHelper.writeNBTBase(nbt, objectType.tagType, obj, getTagName() + "obj");
		}
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (hasObject(nbt)) {
			objectType = ObjectType.values()[nbt.getInteger(getTagName() + "type")];
			obj = NBTHelper.readNBTBase(nbt, objectType.tagType, getTagName() + "obj");
		}
	}
}
