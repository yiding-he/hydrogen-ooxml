package com.hyd.utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Function;

public class IterableTreeWalker<N> implements Iterable<N> {

    private final N root;

    private final Function<N, Collection<N>> childrenFunction;

    public IterableTreeWalker(N root, Function<N, Collection<N>> childrenFunction) {
        this.root = root;
        this.childrenFunction = childrenFunction;
    }

    @Override
    public Iterator<N> iterator() {

        final Stack<N> nStack = new Stack<>();
        if (this.root != null) {
            nStack.push(this.root);
        }

        return new Iterator<N>() {

            @Override
            public boolean hasNext() {
                return !nStack.isEmpty();
            }

            @Override
            public N next() {
                if (nStack.isEmpty()) {
                    return null;
                }

                N next = nStack.pop();
                childrenFunction.apply(next).forEach(nStack::push);
                return next;
            }
        };
    }
}
