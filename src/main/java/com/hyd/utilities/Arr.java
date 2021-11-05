package com.hyd.utilities;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.stream.Stream;

public class Arr {

    /////////////////////////////////////////////////////////////////// convert array

    // Type2[] arr = Arr.convert(arr1).toArrayOf(Type2.class).with(Type1::toType2);
    public static <T> ConvertContext1<T> convert(T[] arr) {
        return new ConvertContext1<>(arr);
    }

    public static class ConvertContext1<T1> {

        private final T1[] source;

        public ConvertContext1(T1[] source) {
            this.source = source;
        }

        public <T2> ConvertContext2<T1, T2> toArrayOf(Class<? extends T2> targetType) {
            return new ConvertContext2<>(this, targetType);
        }
    }

    public static class ConvertContext2<T1, T2> {

        private final ConvertContext1<T1> sourceContext;

        private final Class<? extends T2> targetType;

        public ConvertContext2(ConvertContext1<T1> sourceContext, Class<? extends T2> targetType) {
            this.sourceContext = sourceContext;
            this.targetType = targetType;
        }

        @SuppressWarnings("unchecked")
        public T2[] with(Function<T1, T2> converter) {
            Stream<T2> t2Stream = Stream.of(sourceContext.source).map(converter);
            return t2Stream.toArray(value -> (T2[]) Array.newInstance(targetType, value));
        }
    }

    ///////////////////////////////////////////////////////////////////
}
