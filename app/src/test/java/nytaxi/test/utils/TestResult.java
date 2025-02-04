package nytaxi.test.utils;

import java.time.LocalDateTime;
import java.util.Map;

public record TestResult(
    LocalDateTime pickupTime,
    LocalDateTime dropoffTime,
    Map<Integer, Double> averages
) {}
