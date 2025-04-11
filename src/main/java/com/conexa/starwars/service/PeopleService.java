package com.conexa.starwars.service;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.people.People;
import com.conexa.starwars.dto.people.PeopleDetail;
import com.conexa.starwars.dto.people.PeopleDetailResponse;
import com.conexa.starwars.dto.people.PeopleSearchResponse;

public interface PeopleService {

    public ApiListResponse<People> getAllPeople(Integer page);
    public PeopleDetail getPeopleById(String id);
    public PeopleSearchResponse searchPeopleByName(String name);
}
