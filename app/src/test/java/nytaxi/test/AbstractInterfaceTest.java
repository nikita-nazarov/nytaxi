package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.test.utils.TestUtils;
import nytaxi.utils.Utils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class AbstractInterfaceTest extends AbstractAverageDistancesTest {
    private static Path parquetTmpFilePath;

    AbstractInterfaceTest(@NonNull AverageDistances averageDistances) {
        super(averageDistances);
    }

    @BeforeAll
    public static void setUp() throws Exception {
        parquetTmpFilePath = TestUtils.createTmpParquetFileFromCsv(
                "interface", TestUtils.TRIPS_CSV_FILE_NAME
        );
    }

    @Test
    public void testNotInitialised() {
        assertIsInvalid(calculateDistances());
    }

    @Test
    public void testOpenCloseOpen() throws Exception {
        try (averageDistances) {
            init();
            assertIsValid(calculateDistances());
            averageDistances.close();
            init();
            assertIsValid(calculateDistances());
        }
        assertIsInvalid(calculateDistances());
    }

    @Test
    public void testMultipleInvocations() throws Exception {
        try (averageDistances) {
            init();
            for (int i = 0; i < 3; i++) {
                assertIsValid(calculateDistances());
            }
        }
    }

    @Test
    public void testInvalidRange() throws Exception {
        try (averageDistances) {
            init();
            LocalDateTime epoch = LocalDate.EPOCH.atStartOfDay();
            Map<Integer, Double> result = averageDistances.getAverageDistances(epoch, epoch);
            Assertions.assertNotNull(result);
            Assertions.assertTrue(result.isEmpty());
        }
    }

    @Test
    public void testMultipleInit() throws Exception {
        try (averageDistances) {
            for (int i = 0; i < 3; i++) {
                averageDistances.init(parquetTmpFilePath);
            }
        }
    }

    @Nullable
    private Map<Integer, Double> calculateDistances(){
        return averageDistances.getAverageDistances(
                Utils.fromMicros(0),
                Utils.fromMicros(Long.MAX_VALUE)
        );
    }

    private static void assertIsValid(@Nullable Map<Integer, Double> result) {
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    private static void assertIsInvalid(@Nullable Map<Integer, Double> result) {
        Assertions.assertTrue(result == null || result.isEmpty());
    }

    private void init() {
        averageDistances.init(parquetTmpFilePath.getParent());
    }
}
