package com.example.finalproject.error_handlers;

import android.app.Activity;
import android.widget.Toast;

public class ErrorAPICall {
    public static void handleErrorResponse(Activity activity, int statusCode, String message) {
        String errorMessage = "Response error: " + statusCode + " - " + message;
        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
    }

    public static void handleNetworkError(Activity activity, Throwable t) {
        String errorMessage = "Network error: " + t.getMessage();
        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
    }
}
