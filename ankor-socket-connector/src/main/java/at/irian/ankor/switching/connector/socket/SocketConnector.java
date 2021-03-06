package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.Connector;
import at.irian.ankor.switching.connector.ConnectorRegistry;
import at.irian.ankor.serialization.MessageMapper;
import at.irian.ankor.serialization.MessageMapperFactory;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.system.AnkorSystem;

import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketConnector implements Connector {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnector.class);

    private boolean enabled;
    private SocketListener socketListener;
    private Switchboard switchboard;
    private ConnectorRegistry plug;
    private URI localAddress;
    private MessageMapper<String> messageMapper;

    @Override
    public void init(final AnkorSystem system) {
        this.enabled = system.getConfig().getBoolean("at.irian.ankor.switching.connector.socket.SocketConnector.enabled");
        if (!enabled) {
            LOG.info("SocketConnector not initialized because it is disabled - see ./ankor-socket-connector/src/main/resources/reference.conf for details on how to enable socket support");
            return;
        }

        this.localAddress = URI.create(system.getConfig().getString("at.irian.ankor.switching.connector.socket.SocketConnector.localAddress"));
        this.messageMapper = new MessageMapperFactory<String>(system).createMessageMapper();

        this.switchboard = system.getSwitchboard();
        this.plug = system.getConnectorPlug();

        this.socketListener = new SocketListener(system.getSystemName(),
                                                 this.localAddress,
                                                 this.messageMapper,
                                                 this.switchboard,
                                                 SimpleELPathSyntax.getInstance());
        LOG.info("SocketConnector initialized");
    }

    @Override
    public void start() {
        if (!enabled) {
            LOG.debug("SocketConnector not started because it is disabled");
            return;
        }

        LOG.info("Starting SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.start();

        plug.registerTransmissionHandler(SocketModelAddress.class,
                                         new SocketTransmissionHandler(localAddress, messageMapper, switchboard));
        plug.registerConnectionHandler(SocketModelAddress.class, new SocketConnectionHandler(localAddress, messageMapper));

        LOG.debug("SocketConnector successfully started");
    }

    @Override
    public void stop() {
        if (!enabled) {
            return;
        }

        LOG.info("Stopping SocketConnector (listening on port " + socketListener.getListenPort() + ")");
        socketListener.stop();

        plug.unregisterTransmissionHandler(SocketModelAddress.class);
        plug.unregisterConnectionHandler(SocketModelAddress.class);

        LOG.debug("SocketConnector was stopped");
    }

}
