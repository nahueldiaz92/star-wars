package com.conexa.starwars.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.PaginatedResult;
import com.conexa.starwars.dto.people.People;
import com.conexa.starwars.dto.people.PeopleDetail;
import com.conexa.starwars.dto.people.PeopleDetailResponse;
import com.conexa.starwars.dto.people.PeopleResult;
import com.conexa.starwars.exception.ApiException;
import com.conexa.starwars.external.PeopleApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class PeopleServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PeopleApiClient peopleApiClient;

    @InjectMocks
    private PeopleServiceImpl peopleService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void getAllPeople_ReturnsValidResponse() {
        ApiListResponse<People> mockResponse = new ApiListResponse<>();
        mockResponse.setResults(Arrays.asList(new People(), new People()));
        mockResponse.setTotal_pages(2);

        ResponseEntity<ApiListResponse<People>> responseEntity =
                ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ApiListResponse<People> result = peopleService.getAllPeople(1, 10);

        assertNotNull(result);
        assertEquals(2, result.getResults().size());
    }

    @Test
    public void getAllPeople_ThrowsExceptionWhenPageLessThan1() {
        assertThrows(ApiException.class, () -> {
            peopleService.getAllPeople(0, 10);
        });
    }

    @Test
    public void getAllPeople_ThrowsExceptionWhenPageSizeLessThan1() {
        assertThrows(ApiException.class, () -> {
            peopleService.getAllPeople(1, 0);
        });
    }

    @Test
    public void getAllPeople_ThrowsExceptionWhenPageNotFound() {
        ApiListResponse<People> mockResponse = new ApiListResponse<>();
        mockResponse.setTotal_pages(2);

        ResponseEntity<ApiListResponse<People>> responseEntity =
                ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> {
            peopleService.getAllPeople(3, 10);
        });
    }

    @Test
    public void getAllPeople_ThrowsExceptionWhenInvalidResponse() {
        ResponseEntity<ApiListResponse<People>> responseEntity =
                ResponseEntity.ok(new ApiListResponse<>());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> {
            peopleService.getAllPeople(1, 10);
        });
    }

    @Test
    public void getPeopleById_ReturnsValidPeopleDetail() {
        PeopleDetailResponse response = new PeopleDetailResponse();
        PeopleResult result = new PeopleResult();
        result.setProperties(new PeopleDetail());
        response.setResult(result);

        ResponseEntity<PeopleDetailResponse> responseEntity =
                ResponseEntity.ok(response);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        PeopleDetail detail = peopleService.getPeopleById("1");

        assertNotNull(detail);
    }

    @Test
    public void getPeopleById_ThrowsExceptionWhenPeopleNotFound() {
        PeopleDetailResponse response = new PeopleDetailResponse();
        response.setMessage("not found");

        ResponseEntity<PeopleDetailResponse> responseEntity =
                ResponseEntity.ok(response);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> {
            peopleService.getPeopleById("999");
        });
    }

    @Test
    public void getPeopleById_ThrowsExceptionWhenInvalidResponse() {
        ResponseEntity<PeopleDetailResponse> responseEntity =
                ResponseEntity.ok(new PeopleDetailResponse());

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> {
            peopleService.getPeopleById("1");
        });
    }

    @Test
    public void searchPeopleByName_ReturnsPaginatedResults() {
        // Arrange
        List<PeopleResult> mockResults = Arrays.asList(
                new PeopleResult(),
                new PeopleResult(),
                new PeopleResult() // 3 items total
        );

        when(peopleApiClient.fetchAllPeopleByName("luke")).thenReturn(mockResults);

        PaginatedResult<PeopleResult> result = peopleService.searchPeopleByName("Luke", 1, 2);

        assertNotNull(result);
        assertEquals(2, result.getItems().size()); // Debería devolver 2 items (pageSize = 2)
        assertEquals(1, result.getCurrentPage());
        assertEquals(3, result.getTotalItems()); // Total de items sin paginar
        assertEquals(2, result.getTotalPages()); // 3 items / pageSize 2 = 2 páginas
        assertEquals(2, result.getPageSize());
    }

    @Test
    public void searchPeopleByName_ThrowsExceptionWhenPageLessThan1() {
        assertThrows(ApiException.class, () -> {
            peopleService.searchPeopleByName("Luke", 0, 10);
        });
    }

    @Test
    public void searchPeopleByName_ThrowsExceptionWhenPageSizeLessThan1() {
        assertThrows(ApiException.class, () -> {
            peopleService.searchPeopleByName("Luke", 1, 0);
        });
    }

    @Test
    public void searchPeopleByName_ThrowsExceptionWhenPageNotFound() {
        List<PeopleResult> mockResults = Arrays.asList(new PeopleResult());
        when(peopleApiClient.fetchAllPeopleByName("luke")).thenReturn(mockResults);

        assertThrows(ApiException.class, () -> {
            peopleService.searchPeopleByName("Luke", 2, 10);
        });
    }
}