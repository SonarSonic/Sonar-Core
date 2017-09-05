package sonar.core.network.sync;

public class DirtyPart implements IDirtyPart {

	protected ISyncableListener listener;

    public DirtyPart() {
    }

    @Override
	public DirtyPart setListener(ISyncableListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public ISyncableListener getListener() {
		return listener;
	}

	public void markChanged() {
		if (listener != null)
			listener.markChanged(this);
	}
}
