package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import com.example.finalproject.error_handlers.ErrorAPICall;
import com.example.finalproject.stockData.Company;
import com.example.finalproject.stockData.StockData;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.service.DateValidator;
import com.example.finalproject.service.Service;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DetailActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private static final Service service = new Service();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<StockData> currentStockData;
    private final DateValidator validator = new DateValidator();
    private Company company;
    private ProgressBar loadingIndicator;
    private TextView loadingMessage;
    private TextView companyTextView;
    private TextView selectedCompanyName;
    private TextView dataInfoTextView;

    private CandleStickChart candleStickChart;
    private PopupWindow popupWindow;
    private List<StockData> stockDataList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingMessage = findViewById(R.id.loadingMessage);
        initializeDateRangeButtons();
        String ticker = getIntent().getStringExtra("TICKER");
        if (ticker != null) {
            fetchCompanyData(ticker);
            fetchAllStockPrices(ticker);
        }
        companyTextView =findViewById(R.id.companyInfoTextView);
        candleStickChart = findViewById(R.id.candle_stick_chart);
        selectedCompanyName = findViewById(R.id.companyNameTextView);
        dataInfoTextView = findViewById(R.id.dataInfoTextView);
        configureCandleStickChart();
        candleStickChart.setOnChartValueSelectedListener(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        candleStickChart.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                if (popupWindow != null && popupWindow.isShowing() && touchY > candleStickChart.getHeight()) {
                    popupWindow.dismiss();
                    return true;
                }
            }
            return false;
        });

        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                if (popupWindow != null && popupWindow.isShowing() && touchY > candleStickChart.getHeight()) {
                    popupWindow.dismiss();
                    return true;
                }
            }
            return false;
        });
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchCompanyData(String ticker) {
        service.fetchCompany(ticker, new Callback<Company>() {
            @Override
            public void onResponse(Call<Company> call, Response<Company> response) {
                if (response.isSuccessful() && response.body() != null) {
                    company = response.body();
                    Log.d("CompanyFetch", "Successfully fetched company info: " + company);
                    runOnUiThread(() -> {
                        displayCompanyInfo(company);
                        displayCompanyName(company);
                    });
                } else {
                    Log.e("CompanyFetch", "Response was not successful. Response code: " + response.code());
                    ErrorAPICall.handleErrorResponse(DetailActivity.this,response.code(),response.message());
                }
            }

            @Override
            public void onFailure(Call<Company> call, Throwable t) {
                ErrorAPICall.handleNetworkError(DetailActivity.this, t);
            }
        });
    }

    private void fetchAllStockPrices(String ticker) {
        showLoadingIndicator(true);
        executorService.execute(() -> {
            service.fetchCompanyStockData(ticker, new Callback<List<StockData>>() {
                @Override
                public void onResponse(Call<List<StockData>> call, Response<List<StockData>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<StockData> fetchedStockPrices = response.body();
                        Log.d("StockDataFetch", "Successfully fetched stock data: " + fetchedStockPrices);
                        runOnUiThread(() -> {
                            showLoadingIndicator(false);
                            currentStockData = fetchedStockPrices;
                            updateDataInfoTextView(currentStockData);
                        });
                    } else {
                        Log.e("StockDataFetch", "Response was not successful. Response code: " + response.code());
                        ErrorAPICall.handleErrorResponse(DetailActivity.this,response.code(),response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<StockData>> call, Throwable t) {
                    ErrorAPICall.handleNetworkError(DetailActivity.this, t);
                }
            });
        });
    }

    private List<StockData> getFilteredData(List<StockData> stockDataList,String range,Company company){
        Log.e("FilterData", "Filtering data based on button click");
        LocalDate startDate = calculateStartDate(range);
        LocalDate validStartDate = validator.validateStartDate(startDate, company.getStartDate());
        LocalDate validEndDate = validator.validateStartDateIsNotAfterToday(LocalDate.now());
        validEndDate = validator.validateEndDate(validEndDate, company.getEndDate());


        LocalDate finalValidEndDate = validEndDate;
        return stockDataList.stream()
                .filter(data -> {
                    LocalDate dataDate = data.getDate();
                    return !dataDate.isBefore(validStartDate) && !dataDate.isAfter(finalValidEndDate);
                })
                .collect(Collectors.toList());
    }

    private void initializeDateRangeButtons() {
        findViewById(R.id.button1W).setOnClickListener(v -> updateUIWithFilteredData("1W"));
        findViewById(R.id.button1M).setOnClickListener(v -> updateUIWithFilteredData("1M"));
        findViewById(R.id.button3M).setOnClickListener(v -> updateUIWithFilteredData("3M"));
        findViewById(R.id.button6M).setOnClickListener(v -> updateUIWithFilteredData("6M"));
        findViewById(R.id.button1Y).setOnClickListener(v -> updateUIWithFilteredData("1Y"));
        findViewById(R.id.button5Y).setOnClickListener(v -> updateUIWithFilteredData("5Y"));
        findViewById(R.id.button10Y).setOnClickListener(v -> updateUIWithFilteredData("10Y"));
        findViewById(R.id.buttonMAX).setOnClickListener(v -> updateUIWithFilteredData("MAX"));
    }
    private void updateUIWithFilteredData(String range) {
        Log.d("FilterData", "Filtering data for range: " + range);
        ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        TextView loadingMessage = findViewById(R.id.loadingMessage);

        if (currentStockData != null && company!=null) {
            loadingIndicator.setVisibility(View.GONE);
            loadingMessage.setVisibility(View.GONE);

            List<StockData> filteredStockData = getFilteredData(currentStockData, range, company);
            displayStockData(filteredStockData);
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
            loadingMessage.setText("Loading data, please wait...");
            loadingMessage.setVisibility(View.VISIBLE);
        }

        if (currentStockData != null && company != null) {
            loadingIndicator.setVisibility(View.GONE);
            loadingMessage.setVisibility(View.GONE);

            List<StockData> filteredStockData = getFilteredData(currentStockData, range, company);
            displayStockData(filteredStockData);
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
            loadingMessage.setText("Loading data, please wait...");
            loadingMessage.setVisibility(View.VISIBLE);
        }
    }

    private LocalDate calculateStartDate(String range) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (range) {
            case "1W":
                startDate = today.minus(1, ChronoUnit.WEEKS);
                break;
            case "1M":
                startDate = today.minus(1, ChronoUnit.MONTHS);
                break;
            case "3M":
                startDate = today.minus(3, ChronoUnit.MONTHS);
                break;
            case "6M":
                startDate = today.minus(6, ChronoUnit.MONTHS);
                break;
            case "1Y":
                startDate = today.minus(1, ChronoUnit.YEARS);
                break;
            case "5Y":
                startDate = today.minus(5, ChronoUnit.YEARS);
                break;
            case "10Y":
                startDate = today.minus(10, ChronoUnit.YEARS);
                break;
            case "MAX":
                //jakas data bo i tak mi wyliczy to automatycznie przy pobieraniu
                startDate = LocalDate.of(1500, 1, 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown range: " + range);
        }

        return startDate;
    }

    private void displayCompanyInfo(Company company) {
        companyTextView.setText("");
        if(company==null){
            companyTextView.setText("No available stock market data");
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(company.getDescription());
            companyTextView.setText(stringBuilder.toString());
        }
    }
    private void displayCompanyName(Company company) {
        selectedCompanyName.setText("");
        if(company==null){
            selectedCompanyName.setText("No available stock market data");
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(company.getName());
            selectedCompanyName.setText(stringBuilder.toString());
        }
    }
    private void updateDataInfoTextView(List<StockData> stockDataList) {
        if (stockDataList != null && !stockDataList.isEmpty()) {
            StockData latestStockData = stockDataList.get(stockDataList.size() - 1);

            String infoText = "<b>Date:</b>    " + latestStockData.getDate() + "<br>" +
                    "<b>Open:</b>    " + latestStockData.getOpen() + "<br>" +
                    "<b>Close:</b>   " + latestStockData.getClose() + "<br>" +
                    "<b>High:</b>    " + latestStockData.getHigh() + "<br>" +
                    "<b>Low:</b>     " + latestStockData.getLow() + "<br>" +
                    "<b>Volume:</b>  " + latestStockData.getVolume();

            dataInfoTextView.setText(Html.fromHtml(infoText));
        } else {
            dataInfoTextView.setText("No available stock data");
        }
    }

    private void displayStockData(List<StockData> stockDataList) {
        runOnUiThread(() -> {
            loadingIndicator.setVisibility(View.GONE);
            loadingMessage.setVisibility(View.GONE);
            candleStickChart.setVisibility(View.VISIBLE);
        });
        ArrayList<CandleEntry> candleEntries = new ArrayList<>();

        this.stockDataList = stockDataList;

        for (int i = 0; i < stockDataList.size(); i++) {
            StockData stockData = stockDataList.get(i);

            candleEntries.add(new CandleEntry((float) i, (float) stockData.getHigh(), (float) stockData.getLow(), (float) stockData.getOpen(), (float) stockData.getClose()));
        }

        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "Stock Data");
        candleDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        candleDataSet.setShadowColor(Color.DKGRAY);
        candleDataSet.setShadowWidth(0.8f);
        candleDataSet.setDecreasingColor(Color.RED);
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(Color.GREEN);
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(Color.LTGRAY);
        candleDataSet.setDrawValues(false);

        CandleData candleData = new CandleData(candleDataSet);
        candleStickChart.setData(candleData);

        candleStickChart.invalidate();
    }
    private void configureCandleStickChart() {
        candleStickChart.getDescription().setEnabled(false);
        candleStickChart.setDragEnabled(true);
        candleStickChart.setScaleEnabled(true);
        candleStickChart.setBackgroundColor(Color.parseColor("#000000"));

        Legend legend = candleStickChart.getLegend();
        legend.setEnabled(false);

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(true);
        rightAxis.setEnabled(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(Color.WHITE);

        xAxis.setValueFormatter(new XAxisDateValueFormatter());

        yAxis.setTextColor(Color.WHITE);
        yAxis.setValueFormatter(new LargeValueFormatter());
        yAxis.setDrawLabels(true);

    }

    public void onValueSelected(Entry e, Highlight h) {
        if (e instanceof CandleEntry && stockDataList != null) {
            CandleEntry candleEntry = (CandleEntry) e;
            int dataIndex = (int) e.getX();

            if (dataIndex >= 0 && dataIndex < stockDataList.size()) {
                StockData stockData = stockDataList.get(dataIndex);

                LocalDate date = stockData.getDate();

                if (date != null) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        String dateString = date.format(formatter);

                        float high = candleEntry.getHigh();
                        float low = candleEntry.getLow();
                        float open = candleEntry.getOpen();
                        float close = candleEntry.getClose();

                        String info = "Date: " + dateString + "\nHigh: " + high + "\nLow: " + low + "\nOpen: " + open + "\nClose: " + close;

                        TextView popupText = popupWindow.getContentView().findViewById(R.id.popup_text);
                        popupText.setText(info);

                        popupWindow.showAtLocation(candleStickChart, Gravity.TOP | Gravity.START, (int) h.getX(), (int) h.getY());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                }
            }
        }
    }


    public void onNothingSelected() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void showLoadingIndicator(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                loadingIndicator.setVisibility(View.VISIBLE);
                loadingMessage.setVisibility(View.VISIBLE);
            } else {
                loadingIndicator.setVisibility(View.GONE);
                loadingMessage.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
