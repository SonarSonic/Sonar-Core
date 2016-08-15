package sonar.core.api;

public class StorageSize {

	public static final StorageSize EMPTY = new StorageSize(0, 0);

	private long stored, max;

	public StorageSize(long stored, long max) {
		this.stored = stored;
		this.max = max;
	}

	public long getStoredFluids() {
		return stored;
	}

	public long getMaxFluids() {
		return max;
	}

	public void addItems(long add) {
		stored += add;
	}

	public void addStorage(long add) {
		max += add;
	}
}