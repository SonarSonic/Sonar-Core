package sonar.core.network.sync;

public interface ISyncableListener {

	public void markChanged(IDirtyPart part);
		
}
