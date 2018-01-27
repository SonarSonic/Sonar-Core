package sonar.core.helpers;

import java.util.Collection;

public class ListHelper {

	public static <T> boolean addWithCheck(Collection<T> list, T toAdd) {
		if (toAdd != null && !list.contains(toAdd)) {
			list.add(toAdd);
			return true;
		}
		return false;
	}
	public static <T> boolean addWithCheck(Collection<T> list, T[] toAdd) {
		boolean wasAdded = false;
		for (T t : toAdd) {
			if (t != null && !list.contains(t)) {
				list.add(t);				
				wasAdded = true;
			}
		}
		return wasAdded;
	}

	public static <T> boolean addWithCheck(Collection<T> list, Collection<T> toAdd) {
		boolean wasAdded = false;
		for (T t : toAdd) {
			if (t != null && !list.contains(t)) {
				list.add(t);
				wasAdded=true;
			}
		}
		return wasAdded;
	}

	public static int[] getOrdinals(Enum[] enums) {
		int[] listTypes = new int[enums.length];
		for (int e = 0; e < listTypes.length; e++) {
			listTypes[e] = enums[e].ordinal();
		}
		return listTypes;
	}
}
