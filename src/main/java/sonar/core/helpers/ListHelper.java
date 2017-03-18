package sonar.core.helpers;

import java.util.Collection;


public class ListHelper {

	public static <T> void addWithCheck(Collection<T> list, Collection<T> toAdd) {
		for (T entity : toAdd) {
			if (!list.contains(entity)) {
				list.add(entity);
			}
		}
	}

}
