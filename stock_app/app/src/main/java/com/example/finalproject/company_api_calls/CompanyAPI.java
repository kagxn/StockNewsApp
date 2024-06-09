package com.example.finalproject.company_api_calls;

import com.example.finalproject.stockData.Company;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyAPI {
    @GET("daily/{symbol}")
    Call<Company> getCompanyData(
            @Path("symbol") String symbol,
            @Query("token") String apiToken
    );
}
