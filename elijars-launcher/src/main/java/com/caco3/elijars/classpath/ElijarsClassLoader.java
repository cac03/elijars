package com.caco3.elijars.classpath;

import java.net.URL;
import java.net.URLClassLoader;

public class ElijarsClassLoader extends URLClassLoader {
    public ElijarsClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
