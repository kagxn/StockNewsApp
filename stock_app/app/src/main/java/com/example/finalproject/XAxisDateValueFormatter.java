package com.example.finalproject;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XAxisDateValueFormatter extends ValueFormatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return dateFormat.format(new Date((long) value));
    }
}