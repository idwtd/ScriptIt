package com.ddoerr.scriptit.api.bus;

import java.util.function.Consumer;

public interface Bus<T> {
    void subscribe(String id, Consumer<T> consumer);

    void unsubscribe(String id, Consumer<T> consumer);

    void publish(String id, T data);
}