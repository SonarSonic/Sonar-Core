package sonar.core.utils;

import java.util.HashMap;

/**a simple profiler to test performance*/
public class SimpleProfiler {

	public static HashMap<String, Long> profiles = new HashMap();

	/**set the current time*/
	public static void start(String key) {
		profiles.put(key, System.nanoTime());
	}

	/**removes the profiler and returns the time elapsed in nano seconds*/
	public static long finish(String key) {
		long before = profiles.get(key);
		profiles.remove(key);
		return System.nanoTime() - before;

	}
}
