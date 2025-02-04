package nytaxi.calculators;

import nytaxi.utils.AverageAccumulators;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ParallelAverageDistances extends InMemoryAverageDistances {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    @Override
    @Nullable
    protected Map<Integer, Double> getAverageDistances(long startMicros, long endMicros) {
        assert trips != null;
        List<Future<AverageAccumulators<Integer>>> futures = new ArrayList<>(CPU_NUM);
        try (ExecutorService service = Executors.newFixedThreadPool(CPU_NUM)) {
            int blockSize = Math.ceilDiv(trips.size(), CPU_NUM);
            for (int i = 0; i < CPU_NUM; i++) {
                int start = blockSize * i;
                int end = Math.min(blockSize * (i + 1), trips.size());
                var future = service.submit(() -> {
                    var averageAccumulators = new AverageAccumulators<Integer>();
                    for (int j = start; j < end; j++) {
                        TaxiTrip trip = trips.get(j);
                        if (trip.isValid(startMicros, endMicros)) {
                            averageAccumulators.get(trip.passengerCount()).add(trip.tripDistance());
                        }
                    }
                    return averageAccumulators;
                });
                futures.add(future);
            }

            var resultAccumulators = new AverageAccumulators<Integer>();
            for (Future<AverageAccumulators<Integer>> future : futures) {
                resultAccumulators.add(future.get());
            }
            return resultAccumulators.resultMap();
        } catch (Exception ex) {
            return null;
        }
    }
}
