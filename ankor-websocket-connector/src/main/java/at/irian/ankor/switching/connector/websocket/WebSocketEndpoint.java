package at.irian.ankor.switching.connector.websocket;


import at.irian.ankor.serialization.MessageDeserializer;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.switching.routing.ModelAddress;
import at.irian.ankor.switching.routing.ModelAddressQualifier;
import at.irian.ankor.system.AnkorSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Endpoint for Websocket-based Ankor applications.
 *
 * @author Thomas Spiegl
 */
public abstract class WebSocketEndpoint extends Endpoint {
    private static Logger LOG = LoggerFactory.getLogger(WebSocketEndpoint.class);

    private static final String CLIENT_ID = WebSocketEndpoint.class.getName() + ".CLIENT_ID";

    protected abstract AnkorSystem getAnkorSystem();

    protected String getPath() {
        return "/websocket/ankor/{clientId}";
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        String clientId = getClientId(session);
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalStateException("No valid client id in path");
        }

        AnkorSystem ankorSystem = getAnkorSystem();

        registerSession(ankorSystem, clientId, session);

        LOG.debug("New client connected {}", clientId);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOG.info("Invalidating session {} because of {}: {}", session.getId(), closeReason.getCloseCode(),
                closeReason.getReasonPhrase());
        unregisterSession(session);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        LOG.error("Invalidating session {} because of error {}", session.getId(), thr.getMessage());
        unregisterSession(session);
    }

    private void registerSession(AnkorSystem ankorSystem, String clientId, Session session) {
        WebSocketSessionRegistry sessionRegistry = WebSocketConnector.getSessionRegistry(ankorSystem);
        if (!sessionRegistry.containsSession(clientId, session)) {
            sessionRegistry.addSession(clientId, session);
            MessageDeserializer<String> messageDeserializer
                    = WebSocketConnector.getSingletonMessageDeserializer(ankorSystem);
            WebSocketListener listener =
                    new WebSocketListener(messageDeserializer, ankorSystem.getSwitchboard(),
                            SimpleELPathSyntax.getInstance(), clientId);
            session.addMessageHandler(listener.getByteMessageHandler());
            session.addMessageHandler(listener.getStringMessageHandler());
        }
    }

    private void unregisterSession(Session session) {
        final String clientId = getClientId(session);
        if (clientId != null) {
            AnkorSystem ankorSystem = getAnkorSystem();
            WebSocketConnector.getSessionRegistry(ankorSystem).removeSession(clientId, session);

            ankorSystem.getSwitchboard().closeQualifyingConnections(new ModelAddressQualifier() {
                @Override
                public boolean qualifies(ModelAddress modelAddress) {
                    return modelAddress instanceof WebSocketModelAddress &&
                           ((WebSocketModelAddress) modelAddress).getClientId().equals(clientId);
                }

                @Override
                public String toString() {
                    return "Qualifier for WebSocketModelAddresses of client " + clientId;
                }
            });
        }
    }

    private String getClientId(Session session) {
        String clientId = (String) session.getUserProperties().get(CLIENT_ID);
        if (clientId == null) {
            clientId = session.getPathParameters().get("clientId");
            session.getUserProperties().put(CLIENT_ID, clientId);
        }
        return clientId;
    }

    protected void setClientId(Session session, String clientId) {
        if (getClientId(session) != null) {
            throw new IllegalStateException("ClientId already set");
        }
        session.getUserProperties().put(CLIENT_ID, clientId);
    }


}
