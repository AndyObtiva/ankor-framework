package at.irian.ankorman.sample1.model.animal;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchFilter {

    private String name;
    private AnimalType type;
    private AnimalFamily family;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    public AnimalFamily getFamily() {
        return family;
    }

    public void setFamily(AnimalFamily family) {
        this.family = family;
    }

}
