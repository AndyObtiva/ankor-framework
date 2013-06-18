package at.irian.ankor.sample.fx;

import at.irian.ankor.core.action.SimpleAction;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.ref.RootRef;
import at.irian.ankor.sample.fx.binding.BindingContext;
import at.irian.ankor.sample.fx.binding.ModelBindings;
import at.irian.ankor.sample.fx.infra.App;
import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.model.AnimalType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Thomas Spiegl
 */
public class MainController implements Initializable {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainController.class);

    @FXML
    private Text userName;
    @FXML
    private TextInputControl name;
    @FXML
    private TextInputControl type;
    @FXML
    private TableView<Animal> animalTable;
    @FXML
    private TableColumn<Animal, String> animalName;
    @FXML
    private TableColumn<Animal, String> animalType;

    private ModelRef tabRef;

    private BindingContext bindingContext;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindingContext = new BindingContext();
/*
        executeAction("OpenAnimalSearchTab", new Callback<>() {
            public Object call(Object o) {
                this.tabRef = (ModelRef) o;
                ModelRef filterRef = tabRef.sub("filter");
                bind("/user/fullName", userName);
                bind(filterRef.sub("animalName"), tabId, name);
                bind(filterRef.sub("animalType"), tabId, type);
            }

        });
*/

        RootRef rootRef = App.getApplication().getRefFactory().rootRef();
        // Bind User Name
        ModelBindings.bind(rootRef.sub("userName"), userName.textProperty(), bindingContext);
        // Bind Filter
        ModelRef tabRef = rootRef.sub("tabs.getTab('0')");
        ModelBindings.bind(tabRef.sub("model.filter.name"), name.textProperty(), bindingContext);
        ModelBindings.bind(tabRef.sub("model.filter.type"), type.textProperty(), bindingContext);

        rootRef.fire(SimpleAction.withName("init"));

        loadAnimals();

        this.tabRef = tabRef;
    }

    private void loadAnimals() {
        animalName.setCellValueFactory(new PropertyValueFactory<Animal, String>("name"));
        animalType.setCellValueFactory(new PropertyValueFactory<Animal, String>("type"));
        List<Animal> animals = new ArrayList<Animal>();
        animals.add(new Animal("Trout", AnimalType.Fish));
        animals.add(new Animal("Salmon", AnimalType.Fish));
        animals.add(new Animal("Pike", AnimalType.Fish));
        animalTable.getItems().setAll(animals);
    }

    @FXML
    protected void search(@SuppressWarnings("UnusedParameters") ActionEvent event) {
    }


}