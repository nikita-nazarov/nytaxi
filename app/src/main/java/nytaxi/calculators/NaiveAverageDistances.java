package nytaxi.calculators;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import nytaxi.utils.AverageAccumulators;

public class NaiveAverageDistances extends InMemoryAverageDistances {
    @Override
    @Nullable
    public Map<Integer, Double> getAverageDistances(long startMicros, long endMicros) {
        assert trips != null;
        try {
            var averageAccumulators = new AverageAccumulators<Integer>();
            for (TaxiTrip trip : trips) {
                if (trip.isValid(startMicros, endMicros)) {
                    averageAccumulators.get(trip.passengerCount()).add(trip.tripDistance());
                }
            }
            return averageAccumulators.resultMap();
        } catch (Exception ignored) {
            return null;
        } 
    }
}
