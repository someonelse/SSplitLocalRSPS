package server.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages events which will be run in the future. Has its own thread since some
 * events may need to be ran faster than the cycle time in the main thread.
 * 
 * @author Graham
 */
public class EventManager implements Runnable {

	/**
	 * A reference to the singleton;
	 */
	private static EventManager singleton = null;

	/**
	 * The waitFor variable is multiplied by this before the call to wait() is
	 * made. We do this because other events may be executed after waitFor is
	 * set (and take time). We may need to modify this depending on event count?
	 * Some proper tests need to be done.
	 */
	private static final double WAIT_FOR_FACTOR = 0.5;

	/**
	 * Gets the event manager singleton. If there is no singleton, the singleton
	 * is created.
	 * 
	 * @return The event manager singleton.
	 */
	public static EventManager getSingleton() {
		if (singleton == null) {
			singleton = new EventManager();
			singleton.thread = new Thread(singleton);
			singleton.thread.start();
		}
		return singleton;
	}

	/**
	 * Initializes the event manager (if it needs to be).
	 */
	public static void initialize() {
		getSingleton();
	}

	/**
	 * A list of events that are being executed.
	 */
	private final List<EventContainer> events;

	/**
	 * The event manager thread. So we can interrupt it and end it nicely on
	 * shutdown.
	 */
	private Thread thread;

	/**
	 * Initializes the event manager.
	 */
	private EventManager() {
		events = new ArrayList<>();
	}

	/**
	 * Adds an event.
	 * 
	 * @param event The event to add.
	 * @param tick  The tick time.
	 */
	public synchronized void addEvent(Event event, int tick) {
		events.add(new EventContainer(event, tick));
		notify();
	}

	/**
	 * Processes events. Works kinda like newer versions of cron.
	 */
	@Override
	public synchronized void run() {
		long waitFor;
		List<EventContainer> remove = new ArrayList<>();

		while (true) {
			waitFor = -1;

			for (EventContainer container : events) {
				if (container.isRunning()) {
					if ((System.currentTimeMillis() - container.getLastRun()) >= container.getTick()) {
						try {
							container.execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (container.getTick() < waitFor || waitFor == -1) {
						waitFor = container.getTick();
					}
				} else {
					remove.add(container);
				}
			}

			for (EventContainer container : remove) {
				events.remove(container);
			}
			remove.clear();

			try {
				if (waitFor == -1) {
					wait(); // wait with no timeout
				} else {
					int decimalWaitFor = (int) Math.ceil(waitFor * WAIT_FOR_FACTOR);
					wait(decimalWaitFor);
				}
			} catch (InterruptedException e) {
				break; // stop running
			}
		}
	}

	/**
	 * Shuts the event manager down.
	 */
	public void shutdown() {
		thread.interrupt();
	}
}
