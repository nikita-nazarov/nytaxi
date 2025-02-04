package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.calculators.NaiveAverageDistances;
import org.junit.jupiter.api.Nested;

public class NaiveAverageDistancesTest {
    private final AverageDistances averageDistances = new NaiveAverageDistances();

    @Nested
    public class InterfaceTest extends AbstractInterfaceTest {
        public InterfaceTest() {
            super(NaiveAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class SmokeTest extends AbstractSmokeTest {
        public SmokeTest() {
            super(NaiveAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class RealDataTest extends AbstractRealDataTest {
        public RealDataTest() {
            super(NaiveAverageDistancesTest.this.averageDistances);
        }
    }
}
