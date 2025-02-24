package com.qronicle.model;

import com.qronicle.entity.Item;

import java.util.Set;

public class SearchResponse {
    private long totalResults;
    private Set<Item> items;

    public SearchResponse() {
    }

    public SearchResponse(long totalResults, Set<Item> items) {
        this.totalResults = totalResults;
        this.items = items;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }
}
