package com.example.finalproject.service;

import android.util.Log;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
@RequiresApi(api = Build.VERSION_CODES.O)
public class DateValidator {
    private static final String TAG = "DateValidator";
    public LocalDate validateStartDate(LocalDate startDate, LocalDate companyStartDate) {
        startDate = adjustForWeekend(startDate, true);
        if (startDate.isBefore(companyStartDate)) {
            Log.i(TAG, "The selected start date is before the company's inception, displaying the entire historical data.");
            return companyStartDate;
        }
        return startDate;
    }

    public LocalDate validateStartDateIsNotAfterToday(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        startDate = adjustForWeekend(startDate, true);
        if (startDate.isAfter(today)) {
            Log.i(TAG, "The selected start date cannot be after today's date.");
            return today;
        }
        return startDate;
    }

    public LocalDate validateEndDate(LocalDate endDate, LocalDate companyEndDate) {
        endDate = adjustForWeekend(endDate, false);
        if (endDate.isAfter(companyEndDate)) {
            Log.i(TAG, "This company is no longer active on the stock exchange, displaying the entire historical data.");
            return companyEndDate;
        }
        return endDate;
    }

    private LocalDate adjustForWeekend(LocalDate date, boolean isStartDate) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (isStartDate) {
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                LocalDate adjustedDate = date.plusDays(2);
                Log.i(TAG, "Start date falls on a Saturday. Adjusted to next Monday: " + adjustedDate);
                return adjustedDate;
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                LocalDate adjustedDate = date.plusDays(1);
                Log.i(TAG, "Start date falls on a Sunday. Adjusted to next Monday: " + adjustedDate);
                return adjustedDate;
            }
        } else {
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                LocalDate adjustedDate = date.minusDays(1);
                Log.i(TAG, "End date falls on a Saturday. Adjusted to previous Friday: " + adjustedDate);

                return adjustedDate;
            } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                LocalDate adjustedDate = date.minusDays(2);
                Log.i(TAG, "End date falls on a Sunday. Adjusted to previous Friday: " + adjustedDate);
                return adjustedDate;
            }
        }
        return date;
    }
}

