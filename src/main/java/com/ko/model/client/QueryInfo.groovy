package com.ko.model.client

import com.ko.model.BaseEntity

/**
 * Created by recovery on 2/25/14.
 */
public class QueryInfo {

    private QueryInfo() {}

    int timeFrom;
    int timeTo;

    int yearlyFrom;
    int yearlyTo;

    int monthlyFrom;
    int monthlyTo;

    int dailyMonth;
    int dailyYear;

    // Daily, Monthly, Yearly
    String queryType = "Daily"

    String categoryA;
    String categoryB;
    String categoryC;
    String product;
    String branch;

    boolean groupByTime;

    public static  QueryInfo $fromJson(String json){
        QueryInfo info = BaseEntity.$fromJson(json)
        return info;
    }
}
