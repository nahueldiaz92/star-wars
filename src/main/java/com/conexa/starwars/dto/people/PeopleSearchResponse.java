package com.conexa.starwars.dto.people;

import lombok.Data;

import java.util.List;

@Data
public class PeopleSearchResponse {

    private String message;
    private List<PeopleResult> result;
}
