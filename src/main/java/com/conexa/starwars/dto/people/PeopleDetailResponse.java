package com.conexa.starwars.dto.people;

import lombok.Data;

@Data
public class PeopleDetailResponse {

    private String message;
    private PeopleResult result;
}
