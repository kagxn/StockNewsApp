package com.example.finalproject.service;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.finalproject.stockData.Company;
import com.example.finalproject.company_api_calls.CompanyAPI;
import com.example.finalproject.stockData.StockData;
import com.example.finalproject.company_api_calls.StockDataAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Service {
    private static final String BASE_URL = "https://api.tiingo.com/tiingo/";
    private static final String API_TOKEN = "9e25ebca1a068e0ad657b7ffcdd045ca1691a48d";
    private final CompanyAPI companyAPI;
    private final StockDataAPI companyStockDataAPI;

    private static final String format = "json";
    private static final Logger logger = Logger.getLogger(Service.class.getName());

    public Service() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();

        this.companyAPI = retrofit.create(CompanyAPI.class);
        this.companyStockDataAPI = retrofit.create(StockDataAPI.class);
    }

    public void fetchCompany(String ticker, Callback<Company> callback) {
        Call<Company> call = companyAPI.getCompanyData(ticker, API_TOKEN);
        call.enqueue(callback);
    }

    public void fetchCompanyStockData(String ticker, Callback<List<StockData>> callback) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Call<List<StockData>> companyCall = companyStockDataAPI.getStockData(ticker, today, format, "daily", API_TOKEN);
        companyCall.enqueue(callback);
    }


    private <T> void handleErrorResponse(Response<T> response) {
        int statusCode = response.code();
        logger.log(Level.SEVERE, "API Error: Response not successful. Status code: " + statusCode);
    }

    private void handleFailure(Throwable t) {
        if (t instanceof IOException) {
            logger.log(Level.SEVERE, "Network error: " + t.getMessage(), t);
        } else {
            logger.log(Level.SEVERE, "Unexpected error: " + t.getMessage(), t);
        }
    }

}