package com.rockyrunstream.ac.service;

import java.util.List;

public class ListResponse<T> {

    private List<T> items;

    private boolean hasMore;

    private ACFilter filter;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public ACFilter getFilter() {
        return filter;
    }

    public void setFilter(ACFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "ListResponse{" +
                "items.size=" + (items == null ? "null" :  items.size()) +
                ", hasMore=" + hasMore +
                '}';
    }
}
