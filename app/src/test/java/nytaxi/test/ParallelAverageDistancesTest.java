package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.calculators.ParallelAverageDistances;
import org.junit.jupiter.api.Nested;

public class ParallelAverageDistancesTest {
    private final AverageDistances averageDistances = new ParallelAverageDistances();

    @Nested
    public class InterfaceTest extends AbstractInterfaceTest {
        public InterfaceTest() {
            super(ParallelAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class SmokeTest extends AbstractSmokeTest {
        public SmokeTest() {
            super(ParallelAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class RealDataTest extends AbstractRealDataTest {
        public RealDataTest() {
            super(ParallelAverageDistancesTest.this.averageDistances);
        }
    }
}
