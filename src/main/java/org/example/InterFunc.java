package org.example;

import java.io.IOException;

public interface InterFunc<T> {

    void consume(T t) throws IOException;
}
