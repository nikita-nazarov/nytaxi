package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.calculators.BinarySearchAverageDistances;
import org.junit.jupiter.api.Nested;

public class BinarySearchAverageDistancesTest {
    private final AverageDistances averageDistances = new BinarySearchAverageDistances();

    @Nested
    public class InterfaceTest extends AbstractInterfaceTest {
        public InterfaceTest() {
            super(BinarySearchAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class SmokeTest extends AbstractSmokeTest {
        public SmokeTest() {
            super(BinarySearchAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class RealDataTest extends AbstractRealDataTest {
        public RealDataTest() {
            super(BinarySearchAverageDistancesTest.this.averageDistances);
        }
    }
}
