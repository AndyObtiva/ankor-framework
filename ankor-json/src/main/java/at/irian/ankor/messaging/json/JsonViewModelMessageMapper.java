package at.irian.ankor.messaging.json;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.SimpleAction;
import at.irian.ankor.action.SimpleParamAction;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static at.irian.ankor.model.ViewModelProperty.createUnreferencedProperty;

/**
 * MessageMapper that can map server-side strongly typed(!) view model objects to json and vice versa.
 *
 * @author Manfred Geiler
 */
public class JsonViewModelMessageMapper implements MessageMapper<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonViewModelMessageMapper.class);

    private ObjectMapper mapper;

    public JsonViewModelMessageMapper() {
        init();
    }

    public void init() {
        SimpleModule module =
                new SimpleModule("ViewModelJsonMessageMapperModule",
                                 new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        module.addDeserializer(Action.class, new ActionDeserializer());
        module.addSerializer(ViewModelProperty.class, new ModelPropertySerializer());
        module.addDeserializer(ViewModelProperty.class, new ModelPropertyDeserializer());

        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(module);

        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        //mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@javaType");
    }

    @Override
    public String serialize(Message msg) {
        try {
            return mapper.writeValueAsString(msg);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot serialize " + msg, e);
        }
    }

    @Override
    public Message deserialize(String serializedMsg) {
        try {
            return mapper.readValue(serializedMsg, Message.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot deserialize " + serializedMsg, e);
        }
    }


    class MessageDeserializer extends StdDeserializer<Message> {

        MessageDeserializer() {
            super(Message.class);
        }

        @Override
        public Message deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode tree = mapper.readTree(jp);
            Class<? extends Message> detectedType = null;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator = tree.fields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element=elementsIterator.next();
                String name = element.getKey();
                if (name.equals("action")) {
                    detectedType = ActionMessage.class;
                    break;
                }
                if (name.equals("change")) {
                    detectedType = ChangeMessage.class;
                    break;
                }
            }
            if (detectedType == null) {
                throw new JsonParseException("Cannot determine Message type", jp.getCurrentLocation());
            }
            return mapper.treeToValue(tree, detectedType);
        }
    }

    class ActionDeserializer extends StdDeserializer<Action> {

        ActionDeserializer() {
            super(Action.class);
        }

        @Override
        public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            ObjectNode tree = mapper.readTree(jp);
            Class<? extends Action> detectedType = null;
            Iterator<Map.Entry<String, JsonNode>> elementsIterator = tree.fields();
            while (elementsIterator.hasNext())
            {
                Map.Entry<String, JsonNode> element=elementsIterator.next();
                String name = element.getKey();
                if (name.equals("params")) { // TODO find a better way
                    detectedType = SimpleParamAction.class;
                    break;
                }
            }
            if (detectedType == null) {
                detectedType = SimpleAction.class;
            }
            return mapper.treeToValue(tree, detectedType);
        }
    }

    class ModelPropertySerializer extends StdSerializer<ViewModelProperty> {

        ModelPropertySerializer() {
            super(ViewModelProperty.class);
        }

        @Override
        public void serialize(ViewModelProperty viewModelProperty, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            Object value = viewModelProperty.get();
            if (value != null) {
                jgen.writeObject(value);
            } else {
                jgen.writeNull();
            }
        }
    }

    class ModelPropertyDeserializer extends StdDeserializer<ViewModelProperty> {

        ModelPropertyDeserializer() {
            super(Ref.class);
        }

        @Override
        public ViewModelProperty deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            TreeNode tree = mapper.readTree(jp);
            Object value = mapper.treeToValue(tree, Object.class);
            //noinspection unchecked
            return createUnreferencedProperty(value);
        }
    }

}
