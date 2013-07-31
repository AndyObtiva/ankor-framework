package at.irian.ankor.event.dispatch;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class AkkaEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AkkaEventDispatcherFactory.class);

    private final AnkorActorSystem ankorActorSystem;

    public AkkaEventDispatcherFactory() {
        this.ankorActorSystem = AnkorActorSystem.create();
    }

    @Override
    public EventDispatcher createFor(final ModelContext modelContext) {

        ankorActorSystem.register(modelContext);

        return new EventDispatcher() {
            @Override
            public void dispatch(ModelEvent event) {
                ankorActorSystem.send(modelContext, event);
            }

            @Override
            public void close() {
                ankorActorSystem.unregister(modelContext);
            }
        };
    }

    @Override
    public void close() {
        ankorActorSystem.close();
    }

}
