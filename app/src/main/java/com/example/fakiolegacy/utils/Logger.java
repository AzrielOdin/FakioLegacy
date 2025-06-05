package com.example.fakiolegacy.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that simulates Firebase-style logging functionality.
 * In a real implementation, this would connect to Firebase or another analytics service.
 */
public class Logger {
    private static final String TAG = "FakioLogger";
    private static boolean isEnabled = true;

    /**
     * Logs a simple event with no parameters
     *
     * @param eventName The name of the event to log
     */
    public static void logEvent(String eventName) {
        logEvent(eventName, null);
    }

    /**
     * Logs an event with parameters
     *
     * @param eventName The name of the event to log
     * @param params A map of parameter names and values
     */
    public static void logEvent(String eventName, Map<String, Object> params) {
        if (!isEnabled) return;

        StringBuilder message = new StringBuilder("EVENT: " + eventName);

        if (params != null && !params.isEmpty()) {
            message.append(" - Params: {");
            boolean first = true;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!first) message.append(", ");
                message.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            message.append("}");
        }

        Log.d(TAG, message.toString());
    }

    /**
     * Logs an error
     *
     * @param errorMessage The error message
     */
    public static void logError(String errorMessage) {
        logError(errorMessage, null);
    }

    /**
     * Logs an error with exception details
     *
     * @param errorMessage The error message
     * @param throwable The exception that caused the error
     */
    public static void logError(String errorMessage, Throwable throwable) {
        if (!isEnabled) return;

        if (throwable != null) {
            Log.e(TAG, "ERROR: " + errorMessage, throwable);
        } else {
            Log.e(TAG, "ERROR: " + errorMessage);
        }
    }

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}