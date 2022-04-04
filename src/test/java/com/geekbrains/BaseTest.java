package com.geekbrains;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class BaseTest {

    public String getResourceAsString(String name) {
        //getClass().getClassLoader().getResourceAsStream(name).
        try {
            return new String(
                    Objects.requireNonNull(getClass().getResourceAsStream(name).readAllBytes()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Can't find resource");
            return null;
        }
    }
}
