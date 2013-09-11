package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.akka.AnkorActorSystem;
import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.AkkaScheduler;
import at.irian.ankor.event.dispatch.AkkaEventDispatcherFactory;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.system.AnkorSystem;
import at.irian.ankor.system.AnkorSystemBuilder;
import at.irian.ankorsamples.todosample.domain.task.TaskRepository;
import at.irian.ankorsamples.todosample.viewmodel.ModelRoot;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class NettyLauncher {

    private static AnkorSystem ankorSystem;
    private static SocketIOMessageBus atmosphereMessageBus;

    public static void main(String[] args) throws InterruptedException {
        startAnkorSystem();
        startNettyServer();
    }

    private static void startNettyServer() throws InterruptedException {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        config.setTransports(Transport.WEBSOCKET, Transport.XHRPOLLING);

        final SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                atmosphereMessageBus.addRemoteSystem(new SocketIORemoteSystem(socketIOClient));
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient socketIOClient) {
                atmosphereMessageBus.removeRemoteSystem(socketIOClient.getSessionId().toString());
            }
        });

        server.addMessageListener(new DataListener<String>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) {
                atmosphereMessageBus.receiveSerializedMessage(s);
            }
        });

        server.start();
        Thread.sleep(Integer.MAX_VALUE);
        server.stop();
    }

    private static void startAnkorSystem() {

        AnkorActorSystem ankorActorSystem;
        ankorSystem = new AnkorSystemBuilder()
                .withName(getName())
                .withBeanResolver(getBeanResolver())
                .withModelRootFactory(getModelRootFactory())
                .withMessageBus((atmosphereMessageBus = new SocketIOMessageBus(new ViewModelJsonMessageMapper())))
                .withDispatcherFactory(new AkkaEventDispatcherFactory((ankorActorSystem = AnkorActorSystem.create())))
                .withScheduler(new AkkaScheduler(ankorActorSystem))
                .createServer();
        ankorSystem.start();
    }

    private static String getName() {
        return "sample-todo-servlet-server";
    }

    private static BeanResolver getBeanResolver() {
        return new BeanResolver() {
            @Override
            public Object resolveByName(String beanName) {
                return null;
            }

            @Override
            public Collection<String> getKnownBeanNames() {
                return Collections.emptyList();
            }
        };
    }

    private static ModelRootFactory getModelRootFactory() {
        return new ModelRootFactory() {

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton("root");
            }

            @Override
            public Object createModelRoot(Ref rootRef) {
                return new ModelRoot(rootRef, new TaskRepository());
            }
        };
    }

}
