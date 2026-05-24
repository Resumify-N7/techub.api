package com.techub.api.dto;

import java.util.List;

public class FeedDTO {
        private List<SummaryListResponseDTO> data;
        private int page;
        private int size;
        private long total;

    public FeedDTO(List<SummaryListResponseDTO> data, int page, int size, long total) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<SummaryListResponseDTO> getData() {
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


