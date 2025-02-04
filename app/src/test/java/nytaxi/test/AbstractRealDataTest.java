package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

public abstract class AbstractRealDataTest extends AbstractDataTest {
    AbstractRealDataTest(@NonNull AverageDistances averageDistances) {
        super("realData", averageDistances);
    }

    @Test
    public void test1() throws Exception {
        runTest("test1");
    }

    @Test
    public void test2() throws Exception {
        runTest("test2");
    }

    @Test
    public void test3() throws Exception {
        runTest("test3");
    }

    @Test
    public void test4() throws Exception {
        runTest("test4");
    }
}
