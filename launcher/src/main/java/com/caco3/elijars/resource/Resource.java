package com.caco3.elijars.resource;

import java.io.InputStream;
import java.nio.file.Path;

public interface Resource {
    InputStream getInputStream();

    Path getPath();
}
