package com.caco3.elijars.resource;

import java.util.List;
import java.util.Optional;

public interface ResourceLoader extends AutoCloseable {

    Optional<Resource> loadByName(String name);

    List<Resource> loadAll();

    @Override
    void close();
}
