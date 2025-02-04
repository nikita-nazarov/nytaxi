package nytaxi.test;

import nytaxi.calculators.AverageDistances;
import nytaxi.test.utils.TestResult;
import nytaxi.test.utils.TestUtils;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

public abstract class AbstractDataTest extends AbstractAverageDistancesTest {
    private static final Double TEST_RESULT_COMPARISON_PRECISION = 0.00001;

    private final String testDir;

    AbstractDataTest(@NonNull String testDir, @NonNull AverageDistances averageDistances) {
        super(averageDistances);
        this.testDir = testDir;
    }

    protected void runTest(@NonNull String testName)
            throws IOException, URISyntaxException {
        Path tmpFile = TestUtils.createTmpParquetFileFromCsv(testDir, TestUtils.TRIPS_CSV_FILE_NAME);
        Path tmpDir = tmpFile.getParent();

        TestResult expected = TestUtils.fromCsvAvgData(testDir, testName);
        try (averageDistances) {
            averageDistances.init(tmpDir);
            Map<Integer, Double> actualAverages = averageDistances.getAverageDistances(
                    expected.pickupTime(),
                    expected.dropoffTime()
            );
            Map<Integer, Double> expectedAverages = expected.averages();

            Assertions.assertNotNull(actualAverages);
            Assertions.assertEquals(expectedAverages.size(), actualAverages.size());
            for (Map.Entry<Integer, Double> entry : expectedAverages.entrySet()) {
                Assertions.assertEquals(
                        entry.getValue(),
                        actualAverages.get(entry.getKey()),
                        TEST_RESULT_COMPARISON_PRECISION
                );
            }
        } finally {
            FileUtils.deleteDirectory(tmpDir.toFile());
        }
    }
}
