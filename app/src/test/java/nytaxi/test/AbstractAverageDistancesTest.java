package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import org.jspecify.annotations.NonNull;

abstract class AbstractAverageDistancesTest {
    protected final AverageDistances averageDistances;

    AbstractAverageDistancesTest(@NonNull AverageDistances averageDistances) {
        this.averageDistances = averageDistances;
    }
}
