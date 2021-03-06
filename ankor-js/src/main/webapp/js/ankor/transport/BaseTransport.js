define([
    "./Message",
    "../Path",
    "../events/BaseEvent",
    "../events/ChangeEvent",
    "../events/ActionEvent"
], function(Message, Path, BaseEvent, ChangeEvent, ActionEvent) {
    var BaseTransport = function() {
        this.outgoingMessages = [];
        this.connected = false;
    };

    BaseTransport.prototype.onConnectionChange = function(connected) {};

    BaseTransport.prototype.init = function(ankorSystem) {
        this.ankorSystem = ankorSystem;
        this.utils = ankorSystem.utils;

        if (this.onConnectionChange) this.onConnectionChange(this.connected);
    };

    BaseTransport.prototype.sendEvent = function(event) {
        var message = new Message(event);
        this.sendMessage(message);
    };

    BaseTransport.prototype.sendMessage = function(message) {

        var stateProps = this.ankorSystem.stateProps;
        if (stateProps) {
            message.stateValues = {};
            for (var i = 0, stateProp; (stateProp = stateProps[i]); i++) {
                message.stateValues[stateProp] = this.ankorSystem.model.getValue(new Path(stateProp));
            }
        }

        this.outgoingMessages.push(message);
        if (this.ankorSystem.debug) {
            message.event.pathString = message.event.path.toString();
            console.log("OUT", message);
        }
    };

    BaseTransport.prototype.receiveMessage = function(message) {
        if (this.ankorSystem.debug) {
            message.event.pathString = message.event.path.toString();
            console.log("IN", message);
        }
        this.ankorSystem.onIncomingEvent(message.event);
    };

    BaseTransport.prototype.decodeMessage = function(parsedJson) {
        var event = null;
        var path = new Path(parsedJson.property);
        
        if (parsedJson.change) {
            event = new ChangeEvent(path, "ankorRemoteEvent", parsedJson.change.type, parsedJson.change.key, parsedJson.change.value);
        }
        else if (parsedJson.action) {
            if (parsedJson.action instanceof Object) {
                event = new ActionEvent(path, "ankorRemoteEvent", parsedJson.action.name, parsedJson.action.params);
            }
            else {
                event = new ActionEvent(path, "ankorRemoteEvent", parsedJson.action, null);
            }
        }
        else {
            event = new BaseEvent(path, "ankorRemoteEvent");
        }
        
        var message = new Message(event);
        
        // unexpected side effects
        if (parsedJson.stateProps && true /* TODO: Check if message is newer */ ) {
            this.ankorSystem.stateProps = message.stateProps = parsedJson.stateProps;
        }

        return message;
    };

    BaseTransport.prototype.encodeMessage = function(message) {
        var event = message.event;
        var jsonMessage = {
            property: event.path.toString()
        };
        
        if (message.stateValues) {
            jsonMessage.stateValues = message.stateValues;
        }
        
        if (event instanceof ActionEvent) {
            jsonMessage.action = {
                name: event.actionName,
                params: event.params
            };
        }
        else if (event instanceof ChangeEvent) {
            jsonMessage.change = {
                type: event.type,
                key: event.key,
                value: event.value
            };
        }
        else if (message.connectParams) {
            jsonMessage.connectParams = message.connectParams;
        }
        return jsonMessage;
    };

    return BaseTransport;
});
