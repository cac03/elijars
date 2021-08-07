package com.caco3.elijars.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SystemPropertyLevelSupplierTest {
    @Test
    void byDefaultLevelIsOff() {
        SystemPropertyLevelSupplier supplier = new SystemPropertyLevelSupplier();

        assertThat(supplier.getLevel())
                .isEqualTo(Logger.Level.OFF);
    }

    @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void levelParsed(Logger.Level level) {
        System.setProperty(SystemPropertyLevelSupplier.PROPERTY_NAME, level.name().toLowerCase(Locale.US));
        try {
            SystemPropertyLevelSupplier supplier = new SystemPropertyLevelSupplier();

            assertThat(supplier.getLevel())
                    .isEqualTo(level);
        } finally {
            System.clearProperty(SystemPropertyLevelSupplier.PROPERTY_NAME);
        }
    }
}