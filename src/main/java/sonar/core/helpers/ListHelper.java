package sonar.core.helpers;

import java.util.Collection;

public class ListHelper {

	public static <T> void addWithCheck(Collection<T> list, T[] toAdd) {
		for (T t : toAdd) {
			if (t != null && !list.contains(t)) {
				list.add(t);
			}
		}
	}

	public static <T> void addWithCheck(Collection<T> list, Collection<T> toAdd) {
		for (T t : toAdd) {
			if (t != null && !list.contains(t)) {
				list.add(t);
			}
		}
	}

	public static int[] getOrdinals(Enum[] enums) {
		int[] listTypes = new int[enums.length];
		for (int e = 0; e < listTypes.length; e++) {
			listTypes[e] = enums[e].ordinal();
		}
		return listTypes;
	}
}
