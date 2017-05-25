package com.hse.streams;

/**
 * Created by kirill on 25.05.17.
 */

public class MyNumber<T extends Number> {
    public T value;

    public MyNumber(T value) {
        this.value = value;
    }
}


// EOF
