package server.util;

public class SimpleTimer {

	private long cachedTime;

	public SimpleTimer() {
		reset();
	}

	public void reset() {
		cachedTime = System.currentTimeMillis();
	}

	public long elapsed() {
		package server.util;

/**
 * A simple utility timer for measuring elapsed time.
 */
public class SimpleTimer {

    private long startTime;

    public SimpleTimer() {
        reset();
    }

    /**
     * Resets the timer to the current time.
     */
    public void reset() {
        startTime = System.nanoTime();
    }

    /**
     * Returns the elapsed time in milliseconds since the last reset.
     */
    public long elapsed() {
        return (System.nanoTime() - startTime) / 1_000_000L;
    }

    /**
     * Returns the elapsed time in nanoseconds since the last reset.
     */
    public long elapsedNanos() {
        return System.nanoTime() - startTime;
    }
}
return System.currentTimeMillis() - cachedTime;
	}
}
