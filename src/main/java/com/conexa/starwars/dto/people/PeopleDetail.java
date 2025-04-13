package com.conexa.starwars.dto.people;

import lombok.Data;

import java.time.Instant;

@Data
public class PeopleDetail {

    private Instant created;
    private Instant edited;
    private String name;
    private String gender;
    private String height;
    private String mass;
    private String hair_color;
    private String skin_color;
    private String eye_color;
    private String homeworld;
    private String birth_year;
    private String url;
}
