package at.irian.ankor.session;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.DispatchThreadAware;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.event.dispatch.EventDispatcherFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class DefaultModelSession implements ModelSession, DispatchThreadAware {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSession.class);

    private final String id;
    private final EventListeners eventListeners;
    private final Deque<EventDispatcher> eventDispatcherStack;
    private final Map<String, Object> modelRoots;
    private Map<String, Object> attributes;

    private volatile Thread dispatchThread;

    DefaultModelSession(String id, EventListeners eventListeners) {
        this.id = id;
        this.eventListeners = eventListeners;
        this.eventDispatcherStack = new ArrayDeque<EventDispatcher>();
        this.modelRoots = new HashMap<String, Object>();
        this.attributes = null;
    }

    public static ModelSession create(EventDispatcherFactory eventDispatcherFactory,
                                      String id,
                                      EventListeners globalEventListeners) {
        EventListeners eventListeners = new ArrayListEventListeners(globalEventListeners);
        DefaultModelSession modelSession = new DefaultModelSession(id, eventListeners);
        modelSession.pushEventDispatcher(eventDispatcherFactory.createFor(modelSession));
        return modelSession;
    }

    public String getId() {
        return id;
    }

    @Override
    public EventListeners getEventListeners() {
        return eventListeners;
    }

    @Override
    public Object getModelRoot(String rootName) {
        return modelRoots.get(rootName);
    }

    @Override
    public void setModelRoot(String rootName, Object modelRoot) {
        this.modelRoots.put(rootName, modelRoot);
    }

    @Override
    public void pushEventDispatcher(EventDispatcher eventDispatcher) {
        eventDispatcherStack.push(eventDispatcher);
    }

    @Override
    public EventDispatcher popEventDispatcher() {
        return eventDispatcherStack.pop();
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcherStack.peek();
    }

    @Override
    public void close() {
        for (EventDispatcher eventDispatcher : eventDispatcherStack) {
            eventDispatcher.close();
        }
        modelRoots.clear();
    }

    @Override
    public String toString() {
        return "DefaultModelSession{" +
               "id='" + id + '\'' +
               '}';
    }

    @Override
    public void setCurrentDispatchThread(Thread dispatchThread) {
        this.dispatchThread = dispatchThread;
    }

    @Override
    public Thread getCurrentDispatchThread() {
        return dispatchThread;
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            //todo  do we have a concurrency issue here?
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }
}