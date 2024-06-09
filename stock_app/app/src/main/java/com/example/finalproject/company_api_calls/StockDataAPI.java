package com.example.finalproject.company_api_calls;

import com.example.finalproject.stockData.StockData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StockDataAPI {
    @GET("daily/{symbol}/prices")
    Call<List<StockData>> getStockData(
            @Path("symbol") String symbol,
            @Query("endDate") String endDate,
            @Query("format") String format,
            @Query("resampleFreq") String resampleFreq,
            @Query("token") String apiToken
    );

}
