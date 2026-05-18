package com.techub.api.dto;

import com.techub.api.domain.Summary;

import java.util.List;

public class FeedDTO {
        private List<Summary> data;
        private int page;
        private int size;
        private long total;

    public FeedDTO(List<Summary> data, int page, int size, long total) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<Summary> getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }
}


