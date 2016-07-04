package sonar.core.network.sync;

public interface IDirtyPart {

	public boolean hasChanged();

	public void setChanged(boolean set);
	
}
