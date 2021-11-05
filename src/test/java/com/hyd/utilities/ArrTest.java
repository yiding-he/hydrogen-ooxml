package com.hyd.utilities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ArrTest {

    @Test
    void convert() {
        Integer[] ints = Arr
            .convert(new String[]{"1", "2", "3"})
            .toArrayOf(Integer.class)
            .with(Integer::parseInt);

        assertArrayEquals(new Integer[]{1, 2, 3}, ints);
    }
}
