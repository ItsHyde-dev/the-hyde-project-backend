package com.theHydeProject.components;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

    private final Dotenv env;

    public DotenvConfig() {
        this.env = Dotenv.load();
    }

    public String get(String key) {
        return this.env.get(key);
    }

}
