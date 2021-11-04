package com.hyd.utilities;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilteredIterable<E> implements Iterable<E> {

    private final Iterator<E> source;

    private final Predicate<E> predicate;

    public FilteredIterable(Iterator<E> source, Predicate<E> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private E current;

            @Override
            public boolean hasNext() {
                while (source.hasNext()) {
                    this.current = source.next();
                    if (predicate.test(this.current)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public E next() {
                return this.current;
            }

        };
    }
}
