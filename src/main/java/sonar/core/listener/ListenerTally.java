package sonar.core.listener;

public class ListenerTally<T extends ISonarListener> {

	public T listener;
	public int[] tallies;

	public ListenerTally(T listener, int types) {
		this.listener = listener;
		this.tallies = new int[types];
	}

	public int getTally(int type) {
		return tallies[type];
	}

	public boolean isValid() {
		return listener != null && listener.isValid() && hasTallies();
	}

	public boolean hasTallies() {
		for (int i : tallies) {
			if (i > 0) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return listener.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ListenerTally) {
			return listener.equals(((ListenerTally) obj).listener);
		}
		return false;
	}
}
