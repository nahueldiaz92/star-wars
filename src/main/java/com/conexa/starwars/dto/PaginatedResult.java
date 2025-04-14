package com.conexa.starwars.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResult<T> {
    private List<T> items;
    private int currentPage;
    private int totalPages;
    private int totalItems;
    private int pageSize;

    public PaginatedResult(List<T> items, int currentPage, int pageSize, int totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }
}
