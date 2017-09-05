package sonar.core.network.sync;

import com.google.common.collect.Lists;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.List;

public abstract class SyncPart extends DirtyPart implements ISyncPart {
	public byte id = -1;
	private String name;
	private List<SyncType> types = Lists.newArrayList(SyncType.SAVE, SyncType.DEFAULT_SYNC);
	
	public SyncPart(int id) {
		super();
		this.id = (byte) id;
	}

	public SyncPart(String name) {
		super();
		this.name = name;
	}

    @Override
	public String getTagName() {
		if (name == null) {
			return String.valueOf(id);
		} else {
			return name;
		}
	}

    @Override
	public boolean canSync(SyncType syncType) {
		SyncType[] array = new SyncType[types.size()];
        return syncType.isType(types.toArray(array));
	}

	public SyncPart addSyncType(SyncType... add) {
		SyncType[] array = types.toArray(new SyncType[types.size()]);
		for (SyncType type : add) {
			if (!type.isType(array)) {
				types.add(type);
			}
		}
		return this;
	}

	public SyncPart removeSyncType(SyncType... remove) {
		SyncType[] array = types.toArray(new SyncType[types.size()]);
		for (SyncType type : remove) {
			if (type.isType(array)) {
				types.remove(type);
			}
		}
		return this;
	}
}
