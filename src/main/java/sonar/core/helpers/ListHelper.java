package sonar.core.helpers;

import java.util.Collection;

public class ListHelper {

	public static <T> void addWithCheck(Collection<T> list, Collection<T> toAdd) {
		for (T entity : toAdd) {
            if (entity != null && !list.contains(entity)) {
				list.add(entity);
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
