package nytaxi.utils;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

public class AverageAccumulators<T> {
    public static class Accumulator {
        double sum = 0;
        int n = 0;

        public void add(double num) {
            sum += num;
            n += 1;
        }

        public double average() {
            return sum / n;
        }
    }

    private final HashMap<T, Accumulator> keyToAccumulator = new HashMap<>();

    @NonNull
    @SuppressWarnings("null")
    public Accumulator get(@NonNull T key) {
        return keyToAccumulator.computeIfAbsent(key, (k) -> new Accumulator());
    }

    @NonNull
    public Map<T, Double> resultMap() {
        HashMap<T, Double> result = new HashMap<>();
        for (Map.Entry<T, Accumulator> entry : keyToAccumulator.entrySet()) {
            result.put(entry.getKey(), entry.getValue().average());
        }
        return result;
    }

    public void add(@NonNull AverageAccumulators<T> other) {
        for (Map.Entry<T, Accumulator> entry : other.keyToAccumulator.entrySet()) {
            keyToAccumulator.merge(
                entry.getKey(),
                entry.getValue(),
                (accumulator, accumulatorOther) -> {
                    accumulator.n += accumulatorOther.n;
                    accumulator.sum += accumulatorOther.sum;
                    return accumulator;
                }
            );
        }
    }
}
