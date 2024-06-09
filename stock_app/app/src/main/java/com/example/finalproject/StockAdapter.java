package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {
    private List<Stock> stockList;

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        public TextView tickerTextView;
        public TextView priceTextView;

        public StockViewHolder(View itemView) {
            super(itemView);
            tickerTextView = itemView.findViewById(R.id.tickerTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }

    public StockAdapter(List<Stock> stockList) {
        this.stockList = stockList;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.tickerTextView.setText(stock.getTicker());
        holder.priceTextView.setText(String.valueOf(stock.getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("TICKER", stock.getTicker());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}

