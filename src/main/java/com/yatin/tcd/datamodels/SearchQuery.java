package com.yatin.tcd.datamodels;

public class SearchQuery {
    public String queryid = "";
    public String query = "";

    public SearchQuery(){}

    public SearchQuery(String queryid, String query) {
        this.queryid = queryid;
        this.query = query;
    }
}
