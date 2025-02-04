package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.calculators.BTreeAverageDistances;
import org.junit.jupiter.api.Nested;

public class BTreeAverageDistancesTest {
    private final AverageDistances averageDistances = new BTreeAverageDistances();

    @Nested
    public class InterfaceTest extends AbstractInterfaceTest {
        public InterfaceTest() {
            super(BTreeAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class SmokeTest extends AbstractSmokeTest {
        public SmokeTest() {
            super(BTreeAverageDistancesTest.this.averageDistances);
        }
    }

    @Nested
    public class RealDataTest extends AbstractRealDataTest {
        public RealDataTest() {
            super(BTreeAverageDistancesTest.this.averageDistances);
        }
    }
}
