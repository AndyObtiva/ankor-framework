package at.irian.ankorman.sample1.model.model;

import at.irian.ankor.application.SimpleApplication;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.json.JsonMessageMapper;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankorman.sample1.model.ModelRoot;
import at.irian.ankorman.sample1.model.Tab;
import at.irian.ankorman.sample1.model.animal.Animal;
import at.irian.ankorman.sample1.model.animal.AnimalFamily;
import at.irian.ankorman.sample1.model.animal.AnimalType;
import at.irian.ankorman.sample1.model.animal.AnimalSearchTabModel;
import at.irian.ankorman.sample1.model.animal.AnimalSelectItems;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ModelRootJsonTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelRootJsonTest.class);

    private JsonMessageMapper mapper;
    private MessageFactory messageFactory;
    private RefFactory rf;

    @Before
    public void setUp() throws Exception {
        SimpleApplication application = SimpleApplication.create(ModelRoot.class);
        messageFactory = new MessageFactory();
        rf = application.getRefFactory();
        mapper = new JsonMessageMapper(rf);
    }

    @Test
    public void test() throws Exception {
        ModelRoot root = new ModelRoot();
        root.setUserName("Max Muster");

        Tab<AnimalSearchTabModel> tab = new Tab<AnimalSearchTabModel>("A1");
        root.getTabs().put("A1", tab);

        AnimalSearchTabModel model = new AnimalSearchTabModel(new AnimalSelectItems(new ArrayList<AnimalType>(), new ArrayList<AnimalFamily>()));
        tab.setModel(model);
        List<Animal> animals = Arrays.asList(new Animal("fish", AnimalType.Fish, AnimalFamily.Accipitridae),
                                             new Animal("bird", AnimalType.Bird, AnimalFamily.Balaenopteridae));
        model.setAnimals(animals);

        String json = mapper.serialize(messageFactory.createChangeMessage(rf.rootRef(), rf.ref("root"), root));
        LOG.info(json);

        Message message = mapper.deserialize(json);
        LOG.info(message.toString());

        json = mapper.serialize(messageFactory.createChangeMessage(rf.rootRef(), rf.ref("root.tabs.A1.model.animals"), animals));
        LOG.info(json);
    }
}
