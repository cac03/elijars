package com.caco3.elijars.maven;

import com.caco3.elijars.utils.Assert;

public abstract class FileNameUtils {
    private FileNameUtils() {
    }

    public static String removeExtension(String fileName) {
        Assert.notNull(fileName, "fileName == null");
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        return fileName.substring(0, lastDotIndex);
    }
}
