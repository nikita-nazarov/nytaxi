package nytaxi.calculators;

import nytaxi.utils.AverageAccumulators;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;

public class BTreeAverageDistances extends SortedTripsAverageDistances {
    private BTree bTree;

    @Override
    public void init(@NonNull Path dataDir) {
        super.init(dataDir);
        bTree = new BTree(trips);
    }

    @Override
    protected @Nullable Map<Integer, Double> getAverageDistances(long startMicros, long endMicros) {
        assert trips != null;

        AverageAccumulators<Integer> averageAccumulators = new AverageAccumulators<>();
        int index = bTree.lowerBound(startMicros);
        if (index == -1) {
            return null;
        }
        // Adjust the index for the case when there are many equal lower bounds.
        while (index > 0 && trips.get(index).pickupTimeMicros() == startMicros) {
            index--;
        }

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

    @Override
    public void close() {
        super.close();
        bTree = null;
    }
}
