package nytaxi.calculators;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public abstract class SortedTripsAverageDistances extends InMemoryAverageDistances {
    @Override
    public void init(@NonNull Path dataDir) {
        super.init(dataDir);
        if (!trips.isEmpty()) {
            trips.sort(null);
        }
    }
}
