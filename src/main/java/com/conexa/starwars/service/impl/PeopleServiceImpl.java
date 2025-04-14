package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.*;
import com.conexa.starwars.exception.ApiException;
import com.conexa.starwars.external.PeopleApiClient;
import com.conexa.starwars.service.PeopleService;
import com.conexa.starwars.util.Constants;
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

    private final RestTemplate restTemplate;
    private final PeopleApiClient peopleApiClient;


    @Override
    @Cacheable(value = Constants.LIST_PEOPLE_CACHE, key = "{#page, #pageSize}")
    public ApiListResponse<People> getAllPeople(Integer page, Integer pageSize) {

        if (page < 1) {
            throw new ApiException("La pagina debe ser mayor a 0", HttpStatus.BAD_REQUEST);
        }

        if (pageSize < 1) {
            throw new ApiException("El numero de items por pagina debe ser mayor a 0", HttpStatus.BAD_REQUEST);
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(Constants.API_BASE_URL + "/people/")
                .queryParam("page", page)
                .queryParam("limit", pageSize);

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
                throw new ApiException("Respuesta no valida desde la API", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return body;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ApiException("La request a la API fallo: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @Cacheable(value = Constants.PEOPLE_CACHE, key = "#id")
    public PeopleDetail getPeopleById(String id) {

        String url = Constants.API_BASE_URL + "/people/" + id;
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
                throw new ApiException("Respuesta no valida desde la API", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return response.getBody().getResult().getProperties();
        } catch (HttpClientErrorException ex) {
            throw new ApiException("La request a la API fallo: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public PaginatedResult<PeopleResult> searchPeopleByName(String name, Integer page, Integer pageSize) {
        int adjustedPage = page - 1;

        if (adjustedPage < 0) {
            throw new ApiException("La pagina debe ser mayor a 0", HttpStatus.BAD_REQUEST);
        }
        if (pageSize < 1) {
            throw new ApiException("El numero de items por pagina debe ser mayor a 0", HttpStatus.BAD_REQUEST);
        }

        try {
            List<PeopleResult> allResults = peopleApiClient.fetchAllPeopleByName(name.toLowerCase());

            List<PeopleResult> paginatedResults = allResults.stream()
                    .skip((long) adjustedPage * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            if (page > Math.ceil((double) allResults.size() / pageSize)) {
                throw new ApiException("Pagina no encontrada", HttpStatus.NOT_FOUND);
            }

            return new PaginatedResult<>(
                    paginatedResults,
                    page,
                    pageSize,
                    allResults.size()
            );
        } catch (HttpClientErrorException ex) {
            throw new ApiException("La request a la API falló: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
