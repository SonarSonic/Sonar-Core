package sonar.core.network.sync;

public interface IDirtyPart {

    ISyncableListener getListener();
	
    IDirtyPart setListener(ISyncableListener listener);
	
	//public boolean hasChanged();

	//public void setChanged(boolean set);
}
