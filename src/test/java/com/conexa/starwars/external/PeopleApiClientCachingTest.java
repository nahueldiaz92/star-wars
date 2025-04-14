package com.conexa.starwars.external;

import com.conexa.starwars.dto.people.PeopleResult;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
class PeopleApiClientCachingTest {

    @Autowired
    private PeopleApiClient peopleApiClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
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
    void fetchAllPeopleByName_CachesResult() {
        String name = "luke";

        String mockApiResponse = "{" +
                "\"message\": \"ok\"," +
                "\"result\": [" +
                "  {" +
                "    \"properties\": {" +
                "      \"name\": \"Luke Skywalker\"" +
                "    }," +
                "    \"_id\": \"5f63a36eee9fd7000499be42\"," +
                "    \"description\": \"A person within the Star Wars universe\"," +
                "    \"uid\": \"1\"," +
                "    \"__v\": 2" +
                "  }" +
                "]" +
                "}";


        mockServer.expect(requestTo(Constants.API_BASE_URL + "/people/?name=" + name))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(mockApiResponse, MediaType.APPLICATION_JSON));

        // 1. Primera llamada (debería hacer la petición HTTP)
        List<PeopleResult> firstCall = peopleApiClient.fetchAllPeopleByName(name);
        assertEquals(1, firstCall.size());
        assertEquals("Luke Skywalker", firstCall.get(0).getProperties().getName());

        // 2. Verifica que se hizo la llamada
        mockServer.verify();

        // 3. Segunda llamada (no debe hacer la llamada HTTP, debe usar caché)
        List<PeopleResult> secondCall = peopleApiClient.fetchAllPeopleByName(name);
        assertEquals(1, secondCall.size());

        // 4. Verifica que está en caché
        Cache cache = cacheManager.getCache(Constants.SEARCH_PEOPLE_CACHE);
        assertNotNull(cache.get(name));
    }
}

