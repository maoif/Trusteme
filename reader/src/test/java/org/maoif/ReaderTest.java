package org.maoif;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class ReaderTest {
    // TODO how to test expected results?

    @Test void simpleDatum() {
        
    }

    @Test void quotes() {

    }

    @Test void symbols() {

    }

    @Test void fixnums() {
        // TODO handle expression equality
        Assertions.assertEquals(1, Reader.read("1"));
    }

    @Test void flonums() {
        // TODO handle expression equality
        Assertions.assertEquals(1, Reader.read("1"));
    }

    @Test void lists() {
        var n = Reader.read(" (1 2 3)");
    }

    @Test void vectors() {
        var v = Reader.read("( #(1 2 3) )");
    }
}
