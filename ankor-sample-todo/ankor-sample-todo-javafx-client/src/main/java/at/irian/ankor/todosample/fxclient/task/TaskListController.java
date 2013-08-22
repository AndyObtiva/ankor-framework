package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.fx.binding.BindingContext;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import at.irian.ankor.todosample.fxclient.App;
import at.irian.ankor.todosample.viewmodel.task.TaskModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static at.irian.ankor.fx.binding.ValueBindingsBuilder.bindValue;

public class TaskListController extends AnkorController {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TaskListController.class);

    private BindingContext bindingContext = new BindingContext();
    private Ref modelRef;

    @FXML public VBox tasksList;
    @FXML public ToggleButton toggleAll;
    @FXML public TextField newTodo;
    @FXML public Label todoCountNum;
    @FXML public Label todoCountText;
    @FXML public Button clearButton;

    @FXML public RadioButton filterAll;
    @FXML public RadioButton filterActive;
    @FXML public RadioButton filterCompleted;
    @FXML public ToggleGroup filterToggleGroup;

    @FXML public Node footerTop;
    @FXML public Node footerBottom;

    @ChangeListener(pattern = "root.model.tasks")
    public void renderTasks(Ref changedProperty) {
        List<LinkedHashMap<String, Object>> tasks = changedProperty.getValue();
        Platform.runLater(new TaskLoader(tasks));
    }

    public void initialize(Ref modelRef) {
        this.modelRef = modelRef;

        bindValue(modelRef.append("toggleAll"))
                .toProperty(toggleAll.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("itemsLeft"))
                .toProperty(todoCountNum.textProperty())
                .forIntegerValue()
                .createWithin(bindingContext);

        bindValue(modelRef.append("itemsLeftText"))
                .toProperty(todoCountText.textProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("itemsCompleteText"))
                .toProperty(clearButton.textProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("clearButtonVisibility"))
                .toProperty(clearButton.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(footerTop.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(footerBottom.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("footerVisibility"))
                .toProperty(toggleAll.visibleProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterAllSelected"))
                .toProperty(filterAll.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterActiveSelected"))
                .toProperty(filterActive.selectedProperty())
                .createWithin(bindingContext);

        bindValue(modelRef.append("filterCompletedSelected"))
                .toProperty(filterCompleted.selectedProperty())
                .createWithin(bindingContext);

        // XXX: Much better performance than bindValue
        RefListeners.addTreeChangeListener(modelRef.append("tasks"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                String name = changedProperty.propertyName();

                if (name.equals("title") || name.equals("completed")) {

                    int index = changedProperty.parent().append("index").getValue();
                    TaskPane node = (TaskPane) tasksList.getChildren().get(index);

                    if (name.equals("title")) {
                        node.setText(changedProperty.<String>getValue());
                    } else if (name.equals("completed")) {
                        node.setSelected(changedProperty.<Boolean>getValue());
                    }
                }
            }
        });

        renderTasks(modelRef.append("tasks"));
    }

    @FXML
    public void newTodo(ActionEvent actionEvent) {
        if (!newTodo.getText().equals("")) {

            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("title", newTodo.getText());

            modelRef.fire(new Action("newTask", params));
            newTodo.clear();
        }
    }

    @FXML
    public void clearTasks(ActionEvent actionEvent) {
        modelRef.fire(new Action("clearTasks"));
    }

    @FXML
    public void toggleAll(ActionEvent actionEvent) {
        modelRef.fire(new Action("toggleAll"));
    }

    @FXML
    public void openIRIAN(ActionEvent actionEvent) {
        App.getServices().showDocument("http://www.irian.at");
    }

    @FXML
    public void openTodoMVC(ActionEvent actionEvent) {
        App.getServices().showDocument("http://todomvc.com/");
    }

    private HashMap<Integer, TaskPane> cache = new HashMap<Integer, TaskPane>();

    private class TaskLoader implements Runnable {

        List<LinkedHashMap<String, Object>> tasks;
        public TaskLoader(List<LinkedHashMap<String, Object>> tasks) { this.tasks = tasks; }

        @Override
        public void run() {
            tasksList.getChildren().clear();

            for (LinkedHashMap<String, Object> task : tasks) {
                TaskModel model = new TaskModel(task);

                int index = model.getIndex();

                TaskPane node;
                if (cache.get(index) == null) {
                    cache.put(index, new TaskPane(modelRef));
                }
                node = cache.get(index);
                node.setModel(model);

                Ref itemRef = modelRef.append("tasks").appendIdx(model.getIndex());

                // bindValue(itemRef.append("title"))
                //         .toProperty(node.textProperty())
                //         .createWithin(listContext);
                //
                // bindValue(itemRef.append("completed"))
                //         .toProperty(node.selectedPrperty())
                //         .createWithin(listContext);
                //

                tasksList.getChildren().add(node);
            }
        }
    }
}