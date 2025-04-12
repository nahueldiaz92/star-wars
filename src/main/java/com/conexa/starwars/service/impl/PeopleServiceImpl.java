package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.*;
import com.conexa.starwars.exception.ApiException;
import com.conexa.starwars.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {

    private static final String API_BASE_URL = "https://www.swapi.tech/api";

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_SEARCH_PAGE_SIZE = 3;

    private static final String PEOPLE_CACHE = "peopleDetail";
    private static final String LIST_PEOPLE_CACHE = "peopleList";
    private static final String SEARCH_PEOPLE_CACHE = "peopleSearch";

    private final RestTemplate restTemplate;

    @Override
    @Cacheable(value = LIST_PEOPLE_CACHE, key = "#page")
    public ApiListResponse<People> getAllPeople(Integer page) {

        if (page < 1) {
            throw new ApiException("La pagina debe ser mayor a 0", HttpStatus.BAD_REQUEST);
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

            if (body != null && body.getTotal_pages() < page) {
                throw new ApiException("Pagina no encontrada", HttpStatus.NOT_FOUND);
            }

            if (response.getBody() == null ||
                    response.getBody().getResults() == null) {
                throw new ApiException("Respuesta no valida desde la API", HttpStatus.BAD_GATEWAY);
            }

            return body;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ApiException("La request a la API fallo: " + ex.getMessage(), HttpStatus.BAD_GATEWAY);
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

            PeopleDetailResponse body = response.getBody();

            if (body != null &&
                    "not found".equals(response.getBody().getMessage())) {
                throw new ApiException("El personaje con el ID " + id + " no fue encontrado", HttpStatus.NOT_FOUND);
            }

            if (body == null ||
                    body.getResult() == null ||
                    body.getResult().getProperties() == null) {
                throw new ApiException("Respuesta no valida desde la API", HttpStatus.BAD_GATEWAY);
            }

            return response.getBody().getResult().getProperties();
        } catch (HttpClientErrorException ex) {
            throw new ApiException("La request a la API fallo: " + ex.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    @Cacheable(value = SEARCH_PEOPLE_CACHE, key = "#name")
    public PaginatedResult<PeopleResult> searchPeopleByName(String name, Integer page) {

        int adjustedPage = page - 1;
        if (adjustedPage < 0) adjustedPage = 0;

        String url = API_BASE_URL + "/people/?name=" + name;
        try {
            ResponseEntity<PeopleSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PeopleSearchResponse>() {
                    }
            );

            PeopleSearchResponse body = response.getBody();

            if (body != null && body.getResult() != null) {
                List<PeopleResult> allResults = body.getResult();

                List<PeopleResult> paginatedResults = allResults.stream()
                        .skip((long) adjustedPage * DEFAULT_SEARCH_PAGE_SIZE)
                        .limit(DEFAULT_SEARCH_PAGE_SIZE)
                        .collect(Collectors.toList());

                return new PaginatedResult<>(
                        paginatedResults,
                        page,
                        DEFAULT_SEARCH_PAGE_SIZE,
                        allResults.size()
                );
            } else {
                throw new ApiException("Error en la API de Star Wars: " +
                        (body != null ? body.getMessage() : "respuesta nula"), HttpStatus.BAD_GATEWAY);
            }
        } catch (HttpClientErrorException ex) {
            throw new ApiException("La request a la API fallo: " + ex.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }
}
