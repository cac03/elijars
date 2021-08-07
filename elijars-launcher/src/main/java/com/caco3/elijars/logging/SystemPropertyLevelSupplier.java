package com.caco3.elijars.logging;

import java.util.Locale;

public class SystemPropertyLevelSupplier implements LevelSupplier {
    public static final String PROPERTY_NAME = "elijars.logging.level";

    @Override
    public Logger.Level getLevel() {
        String property = System.getProperty(PROPERTY_NAME);
        if (property == null) {
            return Logger.Level.OFF;
        }
        return parseProperty(property);
    }

    private static Logger.Level parseProperty(String property) {
        String upperCasedLevel = property.toUpperCase(Locale.US);
        return Logger.Level.valueOf(upperCasedLevel);
    }


}
