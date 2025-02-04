package nytaxi.calculators;

import nytaxi.utils.AverageAccumulators;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class BinarySearchAverageDistances extends SortedTripsAverageDistances {
    @Override
    protected @Nullable Map<Integer, Double> getAverageDistances(long startMicros, long endMicros) {
        assert trips != null;

        AverageAccumulators<Integer> averageAccumulators = new AverageAccumulators<>();
        int index = lowerBound(startMicros);
        for (; index < trips.size(); index++) {
            TaxiTrip trip = trips.get(index);
            if (trip.pickupTimeMicros() > endMicros) {
                break;
            } else if (trip.isValid(startMicros, endMicros)) {
                averageAccumulators.get(trip.passengerCount()).add(trip.tripDistance());
            }
        }
        return averageAccumulators.resultMap();
    }

    private int lowerBound(long key) {
        int left = 0;
        int right = trips.size();
        while (left < right) {
            int middle = (left + right) / 2;
            long target = trips.get(middle).pickupTimeMicros();
            if (target >= key) {
                right = middle;
            } else {
                left = middle + 1;
            }
        }
        return left;
    }
}
