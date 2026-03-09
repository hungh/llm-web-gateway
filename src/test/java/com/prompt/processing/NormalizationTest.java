package com.prompt.processing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class NormalizationTest {

    private Normalization normalization;

    @BeforeEach
    public void setUp() {
        normalization = new Normalization();
    }

    @Test
    public void testNormalizeNullInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> normalization.normalize(null)
        );
        assertEquals("Prompt cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testNormalizeEmptyInput() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> normalization.normalize("")
        );
        assertEquals("Prompt cannot be null or empty", exception.getMessage());
    }

    @Test
    void testNormalizeWhitespaceOnly() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> normalization.normalize("   ")
        );
        assertEquals("Prompt cannot be null or empty", exception.getMessage());
    }

    @Test
    void testNormalizeBasicText() {
        assertEquals("hello world", normalization.normalize("Hello World"));
    }

    @Test
    void testNormalizeWithExtraSpaces() {
        assertEquals("hello world", normalization.normalize("  Hello   World  "));
    }

    @Test
    void testNormalizeWithNewlines() {
        assertEquals("hello world", normalization.normalize("Hello\nWorld"));
        assertEquals("hello world test", normalization.normalize("Hello\nWorld\nTest"));
    }

    @Test
    void testNormalizeMultipleSpaces() {
        assertEquals("hello world", normalization.normalize("Hello    World"));
        assertEquals("test multiple spaces", normalization.normalize("Test    Multiple   Spaces"));
    }

    @Test
    void testNormalizePunctuation() {
        assertEquals("hello world", normalization.normalize("Hello World!!!"));
        assertEquals("what is this", normalization.normalize("What is this???"));
        assertEquals("hello world", normalization.normalize("Hello World."));
    }

    @Test
    void testNormalizePunctuationSpacing() {
        assertEquals("hello world", normalization.normalize("Hello World !"));
        assertEquals("what is this", normalization.normalize("What is this ?"));
        assertEquals("test", normalization.normalize("Test ."));
    }

    @Test
    void testNormalizeTrailingPunctuation() {
        assertEquals("hello world", normalization.normalize("Hello World!"));
        assertEquals("what is this", normalization.normalize("What is this?"));
        assertEquals("test", normalization.normalize("Test."));
    }

    @Test
    void testNormalizeMixedContent() {
        assertEquals("hello world! how are you", 
                    normalization.normalize("  Hello   World!!! How   are  you??  "));
    }

    @Test
    void testNormalizeUnicode() {
        assertEquals("café", normalization.normalize("café"));
        assertEquals("naïve", normalization.normalize("naïve"));
    }

    @Test
    void testNormalizeComplexExample() {
        String input = "  What   is the   meaning   of life???  ";
        String expected = "what is the meaning of life";
        assertEquals(expected, normalization.normalize(input));
    }
}
