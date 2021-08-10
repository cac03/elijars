package com.caco3.elijars.maven;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FileNameUtilsTest {
    @ParameterizedTest
    @MethodSource("extensionRemovalTests")
    void extensionRemoved(String fileName, String expected) {
        String actual = FileNameUtils.removeExtension(fileName);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> extensionRemovalTests() {
        return Stream.of(
                Arguments.arguments("foo.java", "foo"),
                Arguments.arguments("foo.java.bar", "foo.java"),
                Arguments.arguments("abcdef/root/foo.java.bar", "abcdef/root/foo.java"),
                Arguments.arguments("foo", "foo")
        );
    }
}