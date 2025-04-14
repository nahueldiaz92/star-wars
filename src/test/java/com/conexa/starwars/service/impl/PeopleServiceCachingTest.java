package com.conexa.starwars.service.impl;

import com.conexa.starwars.dto.ApiListResponse;
import com.conexa.starwars.dto.people.People;
import com.conexa.starwars.dto.people.PeopleDetail;
import com.conexa.starwars.service.PeopleService;
import com.conexa.starwars.util.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
class PeopleServiceCachingTest {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate); // Crea el mock
    }

    @AfterEach
    void tearDown() {
        // Limpia todos los caches después de cada test
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear(); // Elimina todos los entries del cache
                    }
                });
    }

    @Test
    void getAllPeople_CachesResult() {
        // 1. Respuesta mock de la API
        String mockApiListResponse = "{" +
                "\"message\": \"ok\"," +
                "\"total_pages\": 2," +
                "\"results\": [" +
                "{\"uid\": \"1\", \"name\": \"Luke Skywalker\"}" +
                "]" +
                "}";

        // 2. Configura el mock para la primera llamada HTTP
        mockServer.expect(requestTo("https://www.swapi.tech/api/people/?page=1&limit=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockApiListResponse, MediaType.APPLICATION_JSON));

        // 3. Primera llamada: llena el caché
        ApiListResponse<People> firstCall = peopleService.getAllPeople(1, 10);
        assertEquals(1, firstCall.getResults().size());
        assertEquals("Luke Skywalker", firstCall.getResults().get(0).getName());

        // 4. Verifica que se haya hecho solo una llamada
        mockServer.verify();

        // 5. Segunda llamada con los mismos parámetros: debe usar el caché
        ApiListResponse<People> secondCall = peopleService.getAllPeople(1, 10);
        assertEquals("Luke Skywalker", secondCall.getResults().get(0).getName());

        // 6. Verifica que el valor esté en caché (clave compuesta como array de argumentos)
        Cache cache = cacheManager.getCache(Constants.LIST_PEOPLE_CACHE);

        assertNotNull(cache);
        assertNotNull(cache.get(Arrays.asList(1, 10)));
        // 7. Verifica que no haya habido una segunda llamada HTTP
        mockServer.verify(); // Si se hizo otra llamada, falla aquí
    }

    @Test
    void getPeopleById_CachesResult() {
        // 1. Configura el mock para la primera llamada HTTP
        String mockApiResponse = "{" +
                "\"message\": \"ok\"," +
                "\"result\": {" +
                "\"properties\": {" +
                "\"name\": \"Luke Skywalker\"" +
                "}" +
                "}" +
                "}";

        mockServer.expect(requestTo("https://www.swapi.tech/api/people/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockApiResponse, MediaType.APPLICATION_JSON));

        // 2. Primera llamada (debe llamar a la API y llenar el caché)
        PeopleDetail firstCall = peopleService.getPeopleById("1");
        assertEquals("Luke Skywalker", firstCall.getName());

        // 3. Verifica que se hizo la llamada HTTP
        mockServer.verify();

        // 4. Segunda llamada (debe usar el caché, sin llamar a la API)
        PeopleDetail secondCall = peopleService.getPeopleById("1");
        assertEquals("Luke Skywalker", secondCall.getName());

        // 5. Verifica que el caché se usó
        Cache cache = cacheManager.getCache("peopleDetail");
        assertNotNull(cache.get("1"));

        // 6. Opcional: Verifica que NO hubo más llamadas HTTP
        mockServer.verify(); // Si hubiera una segunda llamada HTTP, fallaría aquí
    }
}