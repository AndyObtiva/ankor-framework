package at.irian.ankor.viewmodel.watch;

import at.irian.ankor.serialization.AnkorIgnore;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ExtendedListWrapper<E> extends AbstractExtendedList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ExtendedListWrapper.class);

    @AnkorIgnore
    protected final List<E> wrappedList;

    public ExtendedListWrapper(List<E> wrappedList) {
        this.wrappedList = wrappedList;
    }

    @Override
    public E get(int index) {
        return wrappedList.get(index);
    }

    @Override
    public int size() {
        return wrappedList.size();
    }

    @Override
    public E set(int index, E element) {
        return wrappedList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        wrappedList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return wrappedList.remove(index);
    }

}
