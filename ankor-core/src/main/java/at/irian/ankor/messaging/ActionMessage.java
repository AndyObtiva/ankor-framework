package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public class ActionMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionMessage.class);

    private String actionPropertyPath;
    private Action action;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected ActionMessage() {}

    protected ActionMessage(String messageId, String actionPropertyPath, Action action) {
        super(messageId);
        this.actionPropertyPath = actionPropertyPath;
        this.action = action;
    }

    public String getActionPropertyPath() {
        return actionPropertyPath;
    }

    public Action getAction() {
        return action;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActionMessage that = (ActionMessage) o;

        if (!actionPropertyPath.equals(that.actionPropertyPath)) {
            return false;
        }
        if (!action.equals(that.action)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionPropertyPath.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", actionPropertyPath='" + actionPropertyPath + '\'' +
               ", action=" + action +
               '}';
    }
}
