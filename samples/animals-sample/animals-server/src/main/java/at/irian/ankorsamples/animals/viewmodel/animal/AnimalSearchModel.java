package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.AnkorWatched;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.watch.ExtendedList;
import at.irian.ankor.viewmodel.watch.ExtendedListWrapper;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

import java.util.ArrayList;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    private final TypedRef<String> panelNameRef;
    private final TypedRef<String> serverStatusRef;

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final FloodControl reloadFloodControl;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    @AnkorBigList(missingElementSubstitute = EmptyAnimal.class,
                  threshold = 1000,
                  initialSize = 10,
                  chunkSize = 10)
    @AnkorWatched(diffThreshold = 20)
    private ExtendedList<Animal> animals;

    public AnimalSearchModel(Ref animalSearchModelRef,
                             TypedRef<String> panelNameRef,
                             TypedRef<String> serverStatusRef,
                             AnimalRepository animalRepository) {
        super(animalSearchModelRef);
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.animalRepository = animalRepository;
        this.filter = new AnimalSearchFilter();
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animals = new ExtendedListWrapper<>(new ArrayList<Animal>());
        this.reloadFloodControl = new FloodControl(animalSearchModelRef, 500L);
        AnkorPatterns.initViewModel(this);
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public ExtendedList<Animal> getAnimals() {
        return animals;
    }

    @ChangeListener(pattern = {".filter.**"})
    public void reloadAnimals() {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {

                // get new list from database
                LOG.info("RELOADING animals ...");
                List<Animal> newAnimalsList = animalRepository.searchAnimals(filter, 0, Integer.MAX_VALUE);
                LOG.info("... finished RELOADING");

                animals.setAll(newAnimalsList);

                // reset server status display
                serverStatusRef.setValue("");
            }
        });
    }

    @ChangeListener(pattern = ".filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        panelNameRef.setValue(new PanelNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = ".filter.type")
    public void animalTypeChanged() {
        Ref familyRef = getRef().appendPath("filter.family");
        Ref familiesRef = getRef().appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(filter.getType(), familyRef, familiesRef);
    }

    @ActionListener(name = "save")
    public void save() {
        String status;
        try {
            for (Animal animal : getAnimals()) {
                animalRepository.saveAnimal(animal);
            }
            status = "Animals successfully saved";
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                LOG.error("Error saving animals ", e);
            }
        }
        serverStatusRef.setValue(status);
    }

    public static class EmptyAnimal extends Animal {
        public EmptyAnimal() {
            super("...", null, null);
        }
    }
}
