package com.example.notification.component;

import org.springframework.stereotype.Component;

import brave.propagation.TraceContext;

@Component
public class TraceContextBinder {

    public TraceContext bind(String traceIdStr, String spanIdStr) {
        if (traceIdStr == null || spanIdStr == null) {
            return null;
        }

        long traceIdHigh = 0L;
        long traceIdLow = 0L;


        if (traceIdStr.length() == 32) {
            String highHex = traceIdStr.substring(0, 16);
            String lowHex = traceIdStr.substring(16);
            
            traceIdHigh = Long.parseUnsignedLong(highHex, 16);
            traceIdLow = Long.parseUnsignedLong(lowHex, 16);
        } else {
            traceIdLow = Long.parseUnsignedLong(traceIdStr, 16);
        }

        long spanId = Long.parseUnsignedLong(spanIdStr, 16);

        return TraceContext.newBuilder()
                .traceIdHigh(traceIdHigh)
                .traceId(traceIdLow)
                .spanId(spanId)
                .sampled(true)
                .build();
    }
}