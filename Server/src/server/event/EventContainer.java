package server.event;

/**
 * Holds extra data for an event (for example the tick time etc).
 * 
 * @author Graham
 */
public class EventContainer {

	/**
	 * The actual event.
	 */
	private final Event event;

	/**
	 * A flag which specifies if the event is running;
	 */
	private boolean isRunning;

	/**
	 * When this event was last run.
	 */
	private long lastRun;

	/**
	 * The tick time in milliseconds.
	 */
	private final int tick;

	/**
	 * The event container.
	 * 
	 * @param evt  The event to wrap.
	 * @param tick The delay between executions in milliseconds.
	 */
	protected EventContainer(final Event evt, final int tick) {
		this.tick = tick;
		this.event = evt;
		this.isRunning = true;
		this.lastRun = System.currentTimeMillis();
		// can be changed to 0 if you want events to run straight away
	}

	/**
	 * Executes the event!
	 */
	public void execute() {
		this.lastRun = System.currentTimeMillis();
		this.event.execute(this);
	}

	/**
	 * Gets the last run time.
	 * 
	 * @return Last time this event ran (in ms).
	 */
	public long getLastRun() {
		return this.lastRun;
	}

	/**
	 * Returns the tick time.
	 * 
	 * @return The tick interval in ms.
	 */
	public int getTick() {
		return this.tick;
	}

	/**
	 * Returns the is running flag.
	 * 
	 * @return Whether the event is still active.
	 */
	public boolean isRunning() {
		return this.isRunning;
	}

	/**
	 * Stops this event.
	 */
	public void stop() {
		this.isRunning = false;
	}
}
