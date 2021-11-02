package com.hyd.utilities;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilteredIterator<E> implements Iterator<E> {

    private final Iterator<E> source;

    private final Predicate<E> predicate;

    private E current;

    public FilteredIterator(Iterator<E> source, Predicate<E> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        while (source.hasNext()) {
            this.current = source.next();
            if (this.predicate.test(this.current)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public E next() {
        return this.current;
    }
}
