package sonar.core.network.sync;

public class DirtyPart implements IDirtyPart {

	private boolean hasChanged = true;

	@Override
	public void setChanged(boolean set) {
		hasChanged = set;
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}
	
}
