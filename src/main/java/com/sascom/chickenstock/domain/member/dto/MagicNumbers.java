package com.sascom.chickenstock.domain.member.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public enum MagicNumbers {
    JPG(0xFF, 0xD8, 0xFF),
    PNG(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);

    private final int[] magicNumbers;

    private MagicNumbers(int... bytes) {
        magicNumbers = bytes;
    }

    // Extracts head bytes from any stream
    public static byte[] extract(InputStream istream, int length) throws IOException {
        try (istream) {  // automatically close stream on return
            byte[] buffer = new byte[length];
            istream.read(buffer, 0, length);
            return buffer;
        }
    }

    public boolean is(byte[] bytes) {
        if (bytes.length < magicNumbers.length)
            throw new RuntimeException("");
        for (int i = 0; i < magicNumbers.length; i++) {
            if (Byte.toUnsignedInt(bytes[i]) != magicNumbers[i]) {
                return false;
            }
        }
        return true;
    }
}
