package com.rockyrunstream.ac.service;

import org.bson.types.ObjectId;

public class ACFilter {

    /**
     * Search string
     */
    private String search;

    /**
     * Pagination property - last loaded object ID
     */
    private ObjectId lastId;

    /**
     * Page size
     */
    private int limit;

    private ACPlane.Type type;
    private ACPlane.Size size;

    public ACPlane.Type getType() {
        return type;
    }

    public void setType(ACPlane.Type type) {
        this.type = type;
    }

    public ACPlane.Size getSize() {
        return size;
    }

    public void setSize(ACPlane.Size size) {
        this.size = size;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ObjectId getLastId() {
        return lastId;
    }

    public void setLastId(ObjectId lastId) {
        this.lastId = lastId;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "ACFilter{" +
                "search='" + search + '\'' +
                ", lastId=" + lastId +
                ", limit=" + limit +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
