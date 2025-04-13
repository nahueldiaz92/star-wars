package com.conexa.starwars.util;

import java.util.regex.Pattern;

public final class Constants {

    public static final String SEARCH_PEOPLE_CACHE = "peopleSearch";
    public static final String PEOPLE_CACHE = "peopleDetail";
    public static final String LIST_PEOPLE_CACHE = "peopleList";

    public static final String API_BASE_URL = "https://www.swapi.tech/api";

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_SEARCH_PAGE_SIZE = 3;



    private Constants() {}
}
