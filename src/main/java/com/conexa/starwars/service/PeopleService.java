package com.conexa.starwars.service;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.*;

public interface PeopleService {

    public ApiListResponse<People> getAllPeople(Integer page);
    public PeopleDetail getPeopleById(String id);
    public PaginatedResult<PeopleResult> searchPeopleByName(String name, Integer page);
}
