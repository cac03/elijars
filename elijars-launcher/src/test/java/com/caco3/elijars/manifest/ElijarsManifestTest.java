package com.caco3.elijars.manifest;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.jar.Manifest;

import static org.assertj.core.api.Assertions.assertThat;

class ElijarsManifestTest {
    private static final String START_CLASS = "org.my.Main";
    private static final String START_MODULE = "org.mymodule";

    private static final String TEST_MANIFEST =
            "Manifest-Version: 1.0\n" +
            "Elijars-Start-Class: " + START_CLASS + "\n" +
            "Elijars-Start-Module: " + START_MODULE + "\n";

    private final ElijarsManifest manifest = ElijarsManifest.of(new Manifest(
            new ByteArrayInputStream(TEST_MANIFEST.getBytes(StandardCharsets.UTF_8))));

    ElijarsManifestTest() throws IOException {
    }

    @Test
    void startClassRead() {
        assertThat(manifest.getStartClassName())
                .isEqualTo(START_CLASS);
    }

    @Test
    void startModuleRead() {
        assertThat(manifest.getStartModule())
                .isEqualTo(START_MODULE);
    }
}