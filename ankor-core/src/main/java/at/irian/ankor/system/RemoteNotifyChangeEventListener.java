package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.gateway.party.LocalParty;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.gateway.msg.ChangeEventGatewayMsg;
import at.irian.ankor.gateway.Gateway;
import at.irian.ankor.gateway.party.Party;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

/**
 * Global ChangeEventListener that relays locally happened {@link ChangeEvent ChangeEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyChangeEventListener.class);

    private final Gateway gateway;
    private final Modifier preSendModifier;

    public RemoteNotifyChangeEventListener(Gateway gateway, Modifier preSendModifier) {
        super(null); //global listener
        this.gateway = gateway;
        this.preSendModifier = preSendModifier;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ChangeEvent event) {
        if (event.isLocalEvent()) {
            Change change = event.getChange();
            Ref changedProperty = event.getChangedProperty();
            Change modifiedChange = preSendModifier.modifyBeforeSend(change, changedProperty);
            ModelSession modelSession = changedProperty.context().modelSession();
            Party sender = new LocalParty(modelSession.getId(), changedProperty.root().propertyName());
            gateway.routeMessage(sender,
                                 new ChangeEventGatewayMsg(changedProperty.path(), modifiedChange));
        }
    }

}
