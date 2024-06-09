package com.example.finalproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class StockFragment extends Fragment {

    private static final String API_KEY = "9e25ebca1a068e0ad657b7ffcdd045ca1691a48d";
    private static final String TAG = "StockFragment";
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private List<Stock> stockList;
    private ArrayList<String> suggestions = new ArrayList<>();
    private AutoCompleteTextView symbolAutoCompleteTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        symbolAutoCompleteTextView = view.findViewById(R.id.symbolAutoCompleteTextView);
        new ReadCSVTask().execute();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, suggestions);
        symbolAutoCompleteTextView.setAdapter(adapter);

        symbolAutoCompleteTextView.setOnItemClickListener((parent, v, position, id) -> {
            String selectedSymbol = (String) parent.getItemAtPosition(position);
            openDetailActivity(selectedSymbol);
        });

        stockList = new ArrayList<>();
        stockAdapter = new StockAdapter(stockList);
        recyclerView.setAdapter(stockAdapter);

        fetchStockData();
    }

    private void fetchStockData() {
        String[] tickers = {"AAPL", "GOOGL", "MSFT", "ABNB", "ADBE", "ADI", "ADSK", "AEP", "AMD", "AMZN",
                "ASML", "AVGO", "AZN", "BIIB", "BKNG", "BKR", "CCEP", "CDNS", "CDW", "CEG",
                "CHTR", "CMCSA", "COST", "CPRT", "CRWD", "CSCO", "CSGP", "CSX", "CTAS", "CTSH", "DASH",
                "DDOG", "DLTR", "DXCM", "EA", "FTNT", "GEHC", "GFS", "GILD", "GOOG", "HON", "NVDA", "INTC", "F", "WMT"};
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                InputStreamReader inputStreamReader = new InputStreamReader(getContext().getAssets().open("supported_tickers.csv"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                bufferedReader.readLine();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] fields = line.split(",");

                    if (fields.length > 5 && fields[5].equals("2024-01-19")) {
                        suggestions.add(fields[0]);
                    }
                }

                bufferedReader.close();
                inputStreamReader.close();

            } catch (IOException e) {
                Log.e("CSV", "Error reading CSV file", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, suggestions);
            symbolAutoCompleteTextView.setAdapter(adapter);
        }
    }

    private void openDetailActivity(String ticker) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("TICKER", ticker);
        startActivity(intent);
    }
}
