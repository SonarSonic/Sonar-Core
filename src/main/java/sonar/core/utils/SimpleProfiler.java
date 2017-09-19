package sonar.core.utils;

import java.util.HashMap;

/**
 * a simple profiler to test performance
 */
public class SimpleProfiler {

    public static HashMap<String, Long> profiles = new HashMap<>();

    /**
     * set the current time
     */
	public static void start(String key) {
		profiles.put(key, System.nanoTime());
	}

    /**
     * removes the profiler and returns the time elapsed in nano seconds
     */
	public static long finish(String key) {
		long before = profiles.get(key);
		profiles.remove(key);
		return System.nanoTime() - before;
    }

    public static void finishMilli(String key) {
        double milli = SimpleProfiler.finish(key) / 1000000.0;
        System.out.println(key + " took " + milli + " milliseconds");
    }

    public static void finishMicro(String key) {
        double micro = SimpleProfiler.finish(key) / 1000.0;
        System.out.println(key + " took " + micro + " microseconds");
	}
}
