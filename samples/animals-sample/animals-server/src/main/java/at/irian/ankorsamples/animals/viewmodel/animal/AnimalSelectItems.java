package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class AnimalSelectItems {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSelectItems.class);

    private List<AnimalType> types;
    private List<AnimalFamily> families;

    private AnimalSelectItems(List<AnimalType> types) {
        this.types = types;
        this.families = Collections.emptyList();
    }

    public List<AnimalType> getTypes() {
        return types;
    }

    public void setTypes(List<AnimalType> types) {
        this.types = types;
    }

    public List<AnimalFamily> getFamilies() {
        return families;
    }

    public void setFamilies(List<AnimalFamily> families) {
        this.families = families;
    }


    public static <T> List<T> createSelectItemsFrom(List<T> list) {
        List<T> selectItems = new ArrayList<>(list.size() + 1);
        selectItems.add(null);
        selectItems.addAll(list);
        return selectItems;
    }

    public static AnimalSelectItems create(List<AnimalType> types) {
        return new AnimalSelectItems(createSelectItemsFrom(types));
    }

}
