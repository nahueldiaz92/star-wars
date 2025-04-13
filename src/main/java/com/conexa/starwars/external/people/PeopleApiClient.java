package com.conexa.starwars.external.people;

import com.conexa.starwars.dto.people.PeopleResult;
import com.conexa.starwars.dto.people.PeopleSearchResponse;
import com.conexa.starwars.exception.ApiException;
import com.conexa.starwars.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeopleApiClient {

    private final RestTemplate restTemplate;


    @Cacheable(value = Constants.SEARCH_PEOPLE_CACHE, key = "#name" )
    public List<PeopleResult> fetchAllPeopleByName(String name) {

        String url = Constants.API_BASE_URL + "/people/?name=" + name;

        try {
            ResponseEntity<PeopleSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<PeopleSearchResponse>() {}
            );

            PeopleSearchResponse body = response.getBody();

            if (body != null && body.getResult() != null) {
                return body.getResult();
            } else {
                throw new ApiException("Error en la API de Star Wars: " +
                        (body != null ? body.getMessage() : "respuesta nula"), HttpStatus.INTERNAL_SERVER_ERROR);
            }        } catch (Exception ex) {
            throw new ApiException("La request a la API falló: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
