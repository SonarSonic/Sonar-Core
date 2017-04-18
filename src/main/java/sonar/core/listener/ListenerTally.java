package sonar.core.listener;

import sonar.core.helpers.ListHelper;

public class ListenerTally<T extends ISonarListener> {

	public ListenerList<T> source;
	public T listener;
	public int[] tallies;

	public ListenerTally(ListenerList<T> source, T listener, int types) {
		this.source = source;
		this.listener = listener;
		this.tallies = new int[types];
	}

	public int getTally(int type) {
		return tallies[type];
	}
	
	/**make sure you update the lists state!*/
	public void addTallies(int amount, Enum...enums) {
		addTallies(amount, ListHelper.getOrdinals(enums));
	}

	/**make sure you update the lists state!*/
	public void addTallies(int amount, int...types) {
		for (int type : types)
			tallies[type] += amount;
	}

	/**make sure you update the lists state!*/
	public void removeTallies(int amount, Enum...enums) {
		removeTallies(amount, ListHelper.getOrdinals(enums));		
	}

	/**make sure you update the lists state!*/
	public void removeTallies(int amount, int...types) {
		for (int type : types)
			tallies[type] -= amount;
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
