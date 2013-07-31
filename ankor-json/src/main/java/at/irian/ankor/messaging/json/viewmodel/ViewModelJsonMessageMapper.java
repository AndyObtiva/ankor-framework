package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.action.Action;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageMapper;
import at.irian.ankor.messaging.json.common.ActionDeserializer;
import at.irian.ankor.messaging.json.common.ActionSerializer;
import at.irian.ankor.messaging.json.common.MessageDeserializer;
import at.irian.ankor.viewmodel.ViewModelProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * MessageMapper that can map server-side strongly typed(!) view model objects to json and vice versa.
 *
 * @author Manfred Geiler
 */
public class ViewModelJsonMessageMapper implements MessageMapper<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapper.class);

    private ObjectMapper mapper;

    public ViewModelJsonMessageMapper() {
        init();
    }

    public void init() {
        SimpleModule module =
                new SimpleModule("ViewModelJsonMessageMapperModule",
                                 new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Message.class, new MessageDeserializer());
        module.addSerializer(Action.class, new ActionSerializer());
        module.addDeserializer(Action.class, new ActionDeserializer());
        module.addSerializer(ViewModelProperty.class, new ViewModelPropertySerializer());
        module.addDeserializer(ViewModelProperty.class, new ViewModelPropertyDeserializer());

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

}