package com.conexa.starwars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class StarWarsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarWarsApiApplication.class, args);
    }
}