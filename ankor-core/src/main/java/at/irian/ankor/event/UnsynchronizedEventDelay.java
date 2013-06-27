package at.irian.ankor.event;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
public class UnsynchronizedEventDelay implements EventDelay {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UnsynchronizedEventDelay.class);

    private final DelayedModelEventListener listener;
    private final ScheduledExecutorService executorService;
    private final long delayMilliseconds;
    private long lastEventTimestamp = 0L;

    public UnsynchronizedEventDelay(ScheduledExecutorService executorService,
                                    DelayedModelEventListener listener,
                                    long delayMilliseconds) {
        this.executorService = executorService;
        this.delayMilliseconds = delayMilliseconds;
        this.listener = listener;
    }

    @Override
    public void processDelayed(final ModelEvent event) {
        lastEventTimestamp = System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @SuppressWarnings("StatementWithEmptyBody")
            @Override
            public void run() {
                if (lastEventTimestamp + delayMilliseconds <= System.currentTimeMillis()) {
                    //noinspection unchecked
                    listener.processImmediately(event);
                }
            }
        };
        executorService.schedule(runnable, delayMilliseconds, TimeUnit.MILLISECONDS);
    }
}