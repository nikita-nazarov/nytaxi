package nytaxi;

import nytaxi.calculators.*;
import nytaxi.utils.Utils;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class Benchmark {
    private static final Path DATA_PATH = Path.of("data/");
    private static final Path RESULT_PATH = Path.of("benchmark/");
    private static final int REPEAT_NUM = 10;
    private static final Query[] QUERIES = {
            new Query(1588222685000000L, 1588224164000000L, 15),
            new Query(1586222685000000L, 1586235198000000L, 81),
            new Query(1586222685000000L, 1586245198000000L, 757),
            new Query(1586222685000000L, 1586365198000000L, 12585),
            new Query(1586222685000000L, 1587224164000000L, 82054),
            new Query(1584222685000000L, 1588224164000000L, 589751),
            new Query(1583222685000000L, 1589224164000000L, 2883311),
            new Query(1580222685000000L, 1589224164000000L, 10345630),
            new Query(0L, Long.MAX_VALUE, 23839125),
    };
    private static final Impl[] IMPLS = {
            new Impl(new NaiveAverageDistances(), "naive.csv"),
            new Impl(new ParallelAverageDistances(), "parallel.csv"),
            new Impl(new BinarySearchAverageDistances(), "binary.csv"),
            new Impl(new BTreeAverageDistances(), "btree.csv")
    };

    private record Query(
            long pickupTimeMicros,
            long dropoffTimeMicros,
            int validTripsNum
    ) {}

    private record Impl(
            AverageDistances averageDistances,
            String fileName
    ) {}

    public static void main(String[] args) throws IOException {
        for (Impl impl : IMPLS) {
            System.out.printf("Benchmarking: %s\n", impl.fileName);
            runBenchmark(impl);
        }
    }

    private static void runBenchmark(@NonNull Impl impl) throws IOException {
        try (AverageDistances averageDistances = impl.averageDistances) {
            averageDistances.init(DATA_PATH);

            File file = new File(RESULT_PATH.toFile(), impl.fileName);
            try (PrintWriter printWriter = new PrintWriter(file)) {
                for (Query query : QUERIES) {
                    long timesSumNanos = 0;
                    for (int i = 0; i < REPEAT_NUM; i++) {
                        timesSumNanos += measureQueryTime(query, averageDistances);
                    }
                    long averageTimeMs = timesSumNanos / REPEAT_NUM;
                    printWriter.printf("%d,%d\n", query.validTripsNum, averageTimeMs);
                }
            }
        }
    }

    private static long measureQueryTime(
            @NonNull Query query,
            @NonNull AverageDistances averageDistances
    ) {
        long start = System.nanoTime();
        averageDistances.getAverageDistances(
                Utils.fromMicros(query.pickupTimeMicros),
                Utils.fromMicros(query.dropoffTimeMicros)
        );
        return System.nanoTime() - start;
    }
}
