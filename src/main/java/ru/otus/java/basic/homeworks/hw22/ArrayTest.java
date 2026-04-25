package ru.otus.java.basic.homeworks.hw22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayTest {
    private ArrayProcessor arrayProcessor;

    @BeforeEach
    public void setUp() {
        arrayProcessor = new ArrayProcessor();
    }


    @Test
    void testGetAfterLastOne_normal() {
        int[] input = {1, 2, 1, 2, 2};
        int[] expected = {2, 2};
        assertArrayEquals(expected, arrayProcessor.getAfterLastOne(input));
    }

    @Test
    void testGetAfterLastOne_oneAtEnd() {
        int[] input = {1, 2, 2, 1};
        int[] expected = {};
        assertArrayEquals(expected, arrayProcessor.getAfterLastOne(input));
    }

    @Test
    void testGetAfterLastOne_exception() {
        int[] input = {2, 2, 2, 2};
        assertThrows(RuntimeException.class, () -> arrayProcessor.getAfterLastOne(input));
    }


    @ParameterizedTest
    @CsvSource({
            "1 2, true",
            "1 1, false",
            "1 3, false",
            "1 2 2 1, true",
            "3 4 5, false",
            "1 1 2, true",
            "2 2 1, true",
            "2 3, false",
            "0 1 2, false",
            "1 1 1, false",
            "2 2 2, false",
            "2 2 2 1, true"
    })
    void testIsArrayValid(String input, boolean expected) {
        String[] parts = input.split(" ");
        int[] inputArray = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            inputArray[i] = Integer.parseInt(parts[i]);
        }
        assertEquals(expected, arrayProcessor.isArrayValid(inputArray));
    }
}
