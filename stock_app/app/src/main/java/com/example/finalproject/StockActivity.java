package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StockActivity extends AppCompatActivity {

    private static final String API_KEY = "9e25ebca1a068e0ad657b7ffcdd045ca1691a48d";
    private static final String TAG = "StockActivity";
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private List<Stock> stockList;
    private ArrayList<String> suggestions = new ArrayList<>();
    private AutoCompleteTextView symbolAutoCompleteTextView;

    @Override
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        symbolAutoCompleteTextView = findViewById(R.id.symbolAutoCompleteTextView);
        new ReadCSVTask().execute();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, suggestions);

        symbolAutoCompleteTextView.setAdapter(adapter);

        symbolAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSymbol = (String) parent.getItemAtPosition(position);
            openDetailActivity(selectedSymbol);
        });

        stockList = new ArrayList<>();
        stockAdapter = new StockAdapter(stockList);
        recyclerView.setAdapter(stockAdapter);

        fetchStockData();
    }

    private void fetchStockData() {
        String[] tickers = {"AAPL", "GOOGL", "MSFT","ABNB",
                "ADBE","ADI","ADSK","AEP","AMD","AMZN","ASML","AVGO","AZN","BIIB","BKNG","BKR","CCEP","CDNS","CDW","CEG","CHTR","CMCSA","COST","CPRT","CRWD","CSCO","CSGP","CSX","CTAS","CTSH","DASH","DDOG","DLTR","DXCM","EA",
        "FTNT","GEHC","GFS","GILD","GOOG","HON","NVDA","INTC","F","WMT"};
        RequestQueue queue = Volley.newRequestQueue(this);
        Gson gson = new Gson();

        for (String ticker : tickers) {
            String url = "https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?token=" + API_KEY;

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JsonObject jsonObject = gson.fromJson(response.get(0).toString(), JsonObject.class);
                                double price = jsonObject.get("close").getAsDouble();
                                stockList.add(new Stock(ticker, price));
                                stockAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Error fetching data for " + ticker, error);
                        }
                    });

            queue.add(jsonArrayRequest);
        }
    }
    public class ReadCSVTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStream inputStream = getAssets().open("supported_tickers.csv");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                bufferedReader.readLine();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] fields = line.split(",");

                    if (fields.length > 5 && fields[5].equals("2024-01-19")) {
                        suggestions.add(fields[0]);
                    }
                }

                inputStream.close();
                inputStreamReader.close();
                bufferedReader.close();

            } catch (IOException e) {
                Log.e("CSV", "Error reading CSV file", e);
            }

            return null;
        }
    }

    private void openDetailActivity(String ticker) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("TICKER", ticker);
        startActivity(intent);
    }
}
