package sonar.core.network.sync;

public interface IDirtyPart {

	public ISyncableListener getListener();
	
	public IDirtyPart setListener(ISyncableListener listener);
	
	//public boolean hasChanged();

	//public void setChanged(boolean set);
	
}
