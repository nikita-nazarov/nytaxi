package nytaxi.calculators;

import nytaxi.utils.Utils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public abstract class InMemoryAverageDistances implements AverageDistances {
    protected List<TaxiTrip> trips;

    @Override
    public void init(@NonNull Path dataDir) {
        trips = Utils.readDataSafe(dataDir);
    }

    @Override
    @Nullable
    public final Map<Integer, Double> getAverageDistances(
            @NonNull LocalDateTime start,
            @NonNull LocalDateTime end
    ) {
        if (trips == null) {
            return null;
        }
        var zoneOffset = ZoneOffset.ofTotalSeconds(0);
        long startMicros = start.toInstant(zoneOffset).toEpochMilli() * 1000;
        long endMicros = end.toInstant(zoneOffset).toEpochMilli() * 1000;
        return getAverageDistances(startMicros, endMicros);
    }

    @Nullable
    protected abstract Map<Integer, Double> getAverageDistances(
            long startMicros, long endMicros
    );

    @Override
    public void close() {
        trips = null;
    }
}
