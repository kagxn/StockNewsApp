package com.example.finalproject.stockData;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class StockData{
    private LocalDate date;
    private double close;
    private double high;
    private double low;
    private double open;
    private long volume;
    private double adjClose;
    private double adjHigh;
    private double adjLow;
    private double adjOpen;
    private long adjVolume;
    private double divCash;
    private double splitFactor;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(double adjClose) {
        this.adjClose = adjClose;
    }

    public double getAdjHigh() {
        return adjHigh;
    }

    public void setAdjHigh(double adjHigh) {
        this.adjHigh = adjHigh;
    }

    public double getAdjLow() {
        return adjLow;
    }

    public void setAdjLow(double adjLow) {
        this.adjLow = adjLow;
    }

    public double getAdjOpen() {
        return adjOpen;
    }

    public void setAdjOpen(double adjOpen) {
        this.adjOpen = adjOpen;
    }

    public long getAdjVolume() {
        return adjVolume;
    }

    public void setAdjVolume(long adjVolume) {
        this.adjVolume = adjVolume;
    }

    public double getDivCash() {
        return divCash;
    }

    public void setDivCash(double divCash) {
        this.divCash = divCash;
    }

    public double getSplitFactor() {
        return splitFactor;
    }

    public void setSplitFactor(double splitFactor) {
        this.splitFactor = splitFactor;
    }
}
