package com.techub.api.dto;

import java.util.List;

public class FollowPageDTO {

    private List<FollowGetResponseDTO> data;
    private int page;
    private int size;
    private long total;

    public FollowPageDTO(List<FollowGetResponseDTO> data, int page, int size, long total) {
        this.data  = data;
        this.page  = page;
        this.size  = size;
        this.total = total;
    }

    public List<FollowGetResponseDTO> getData()  { return data; }
    public int  getPage()                        { return page; }
    public int  getSize()                        { return size; }
    public long getTotal()                       { return total; }
}