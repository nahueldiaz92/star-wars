package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.people.PeopleDetailResponse;
import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.people.People;
import com.conexa.starwars.dto.people.PeopleDetail;
import com.conexa.starwars.dto.people.PeopleSearchResponse;
import com.conexa.starwars.exceptions.ApiException;
import com.conexa.starwars.exceptions.PageNotFoundException;
import com.conexa.starwars.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {

    private static final String API_BASE_URL = "https://www.swapi.tech/api";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String PEOPLE_CACHE = "peopleDetail";
    private static final String LIST_PEOPLE_CACHE = "peopleList";
    private final RestTemplate restTemplate;

    @Override
    @Cacheable(value = LIST_PEOPLE_CACHE, key = "#page")
    public ApiListResponse<People> getAllPeople(Integer page) {

        if (page < 1) {
            throw new IllegalArgumentException("La pagina debe ser mayor a 0");
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(API_BASE_URL + "/people/")
                    .queryParam("page", page)
                    .queryParam("limit", DEFAULT_PAGE_SIZE);

        String url = uriBuilder.toUriString();

        try {
            ResponseEntity<ApiListResponse<People>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiListResponse<People>>() {
                    }
            );

            ApiListResponse<People> body = response.getBody();


            if (body == null || !"ok".equals(body.getMessage())) {
                throw new ApiException("Respuesta no valida desde la API");
            }

            return body;
        } catch (HttpClientErrorException.NotFound e) {
            throw new PageNotFoundException(page);
        }

    }

    @Override
    @Cacheable(value = PEOPLE_CACHE, key = "#id")
    public PeopleDetail getPeopleById(String id) {

        String url = API_BASE_URL + "/people/" + id;
        try {

            ResponseEntity<PeopleDetailResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PeopleDetailResponse>() {
                    }
            );

            if (response.getBody() == null ||
                    response.getBody().getResult() == null ||
                    response.getBody().getResult().getProperties() == null) {
                throw new ApiException("Respuesta no valida desde la API");
            }

            return response.getBody().getResult().getProperties();
        } catch (HttpClientErrorException ex) {
            throw new ApiException("La request a la API fallo con codigo: " + ex.getStatusCode());
        }
    }

    @Override
    public PeopleSearchResponse searchPeopleByName(String name) {

        String url = API_BASE_URL + "/people/?name=" + name;

        ResponseEntity<PeopleSearchResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PeopleSearchResponse>() {}
        );
        System.out.println("====================");
        System.out.println(response.getBody());
        System.out.println("====================");

        return response.getBody();
    }
}
