package com.conexa.starwars.dto;

import lombok.Data;

import java.util.List;
@Data
public class ApiListResponse<T> {

    private String message;
    private int total_records;
    private int total_pages;
    private String previous;
    private String next;
    private List<T> results;
}
