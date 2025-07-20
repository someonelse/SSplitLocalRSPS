package server.task;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import server.task.TaskFactory.Task;

public class TaskManager {

    /*
     * Note: The pool size _must_ remain 1 to keep synchronization proper.
     */
    private static final ScheduledThreadPoolExecutor scheduledExecutor = 
        (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /**
     * Registers a client task to run after a specified delay in seconds.
     * If the task is not global, it sets the current task in the client.
     */
    public static Future<?> registerClientTask(Task task, int delay) {
        Future<?> taskFuture = scheduledExecutor.schedule(task, delay, TimeUnit.SECONDS);
        if (!task.isGlobal())
            task.getClient().setCurrentTask(taskFuture);
        return taskFuture;
    }

    /**
     * Registers a delayed task with the specified time unit.
     */
    public static Future<?> registerDelayedTask(Runnable task, int delay, TimeUnit unit) {
        return scheduledExecutor.schedule(task, delay, unit);
    }

    /**
     * Schedules a recurring task with fixed delay.
     */
    public static Future<?> scheduleTask(Runnable task, int delay, int rate, TimeUnit unit) {
        return scheduledExecutor.scheduleWithFixedDelay(task, delay, rate, unit);
    }
}
