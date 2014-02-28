package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * todo  timeout functionality
 *
 * @author Manfred Geiler
 */
public class DefaultModelConnectionManager implements ModelConnectionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelConnectionManager.class);

    private final Map<String, ModelConnection> connectionMap = new ConcurrentHashMap<String, ModelConnection>();
    private final ModelConnectionFactory modelConnectionFactory;

    public DefaultModelConnectionManager(ModelConnectionFactory modelConnectionFactory) {
        this.modelConnectionFactory = modelConnectionFactory;
    }

    @Override
    public ModelConnection getOrCreate(ModelSession modelSession, RemoteSystem remoteSystem) {
        String connectionId = getConnectionIdFrom(modelSession, remoteSystem);
        ModelConnection modelConnection;
        modelConnection = connectionMap.get(connectionId);
        if (modelConnection == null) {
            synchronized (connectionMap) {
                modelConnection = connectionMap.get(connectionId);
                if (modelConnection == null) {
                    modelConnection = createAndInitConnection(modelSession, remoteSystem);
                    connectionMap.put(connectionId, modelConnection);
                }
            }
        }
        return modelConnection;
    }

    @Override
    public Collection<ModelConnection> getAllFor(ModelSession modelSession) {
        Collection<ModelConnection> result = null;
        for (ModelConnection modelConnection : connectionMap.values()) {
            if (modelConnection.getModelSession().equals(modelSession)) {
                if (result == null) {
                    result = Collections.singleton(modelConnection);
                } else {
                    if (result.size() == 1) {
                        result = new ArrayList<ModelConnection>(result);
                    }
                    result.add(modelConnection);
                }
            }
        }
        return result == null ? Collections.<ModelConnection>emptyList() : result;
    }

    @Override
    public Collection<ModelConnection> getAllFor(RemoteSystem remoteSystem) {
        Collection<ModelConnection> result = null;
        for (Map.Entry<String, ModelConnection> entry : connectionMap.entrySet()) {
            String remoteSystemId = getRemoteSystemIdFromConnectionId(entry.getKey());
            if (remoteSystem.getId().equals(remoteSystemId)) {
                if (result == null) {
                    result = Collections.singleton(entry.getValue());
                } else {
                    if (result.size() == 1) {
                        result = new ArrayList<ModelConnection>(result);
                    }
                    result.add(entry.getValue());
                }
            }
        }
        return result == null ? Collections.<ModelConnection>emptyList() : result;
    }

    private String getConnectionIdFrom(ModelSession modelSession, RemoteSystem remoteSystem) {
        return remoteSystem.getId() + "_" + modelSession.getId();
    }

    private String getRemoteSystemIdFromConnectionId(String connectionId) {
        int i = connectionId.indexOf('_');
        if (i == -1) {
            throw new IllegalArgumentException("invalid connection id " + connectionId);
        }
        return connectionId.substring(0, i);
    }

    protected ModelConnection createAndInitConnection(ModelSession modelSession, RemoteSystem remoteSystem) {
        ModelConnection modelConnection = modelConnectionFactory.create(modelSession, remoteSystem);
        modelConnection.init();
        return modelConnection;
    }

    @Override
    public void invalidate(ModelConnection modelConnection) {
        modelConnection.close();
    }

}