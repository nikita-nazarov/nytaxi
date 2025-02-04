package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

public abstract class AbstractSmokeTest extends AbstractDataTest {
    AbstractSmokeTest(@NonNull AverageDistances averageDistances) {
        super("smoke", averageDistances);
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

    @Test
    public void test5() throws Exception {
        runTest("test5");
    }

    @Test
    public void test6() throws Exception {
        runTest("test6");
    }
}
