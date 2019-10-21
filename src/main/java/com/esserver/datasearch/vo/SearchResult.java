package com.esserver.datasearch.vo;

import lombok.Data;

/**
 * Created by fangyitao on 2018/6/11.
 */
@Data
public class SearchResult {
    private long totalCount;
    private long from;
    private long size;
    private String data;
    private String status;
}
