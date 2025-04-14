package com.conexa.starwars.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.conexa.starwars.dto.people.PeopleResult;
import com.conexa.starwars.dto.people.PeopleSearchResponse;
import com.conexa.starwars.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PeopleApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PeopleApiClient peopleApiClient;

    @Test
    public void fetchAllPeopleByName_ReturnsListOfPeople() {
        PeopleSearchResponse mockResponse = new PeopleSearchResponse();
        mockResponse.setResult(Arrays.asList(new PeopleResult(), new PeopleResult()));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        List<PeopleResult> results = peopleApiClient.fetchAllPeopleByName("Luke");

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void fetchAllPeopleByName_ThrowsExceptionWhenResponseIsNull() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));

        assertThrows(ApiException.class, () -> {
            peopleApiClient.fetchAllPeopleByName("Luke");
        });
    }

    @Test
    public void fetchAllPeopleByName_ThrowsExceptionWhenResultIsNull() {
        PeopleSearchResponse mockResponse = new PeopleSearchResponse();

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        assertThrows(ApiException.class, () -> {
            peopleApiClient.fetchAllPeopleByName("Luke");
        });
    }

    @Test
    public void fetchAllPeopleByName_ThrowsExceptionWhenApiFails() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ApiException.class, () -> {
            peopleApiClient.fetchAllPeopleByName("Luke");
        });
    }
}
