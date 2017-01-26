package sonar.core.network.sync;

public interface ICheckableSyncPart<T extends ISyncPart> extends ISyncPart {

	public boolean equalPart(T part);
}
