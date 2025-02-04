package nytaxi.test.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import nytaxi.utils.Constants;
import nytaxi.utils.Utils;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.util.HadoopOutputFile;
import org.apache.parquet.io.OutputFile;
import nytaxi.calculators.TaxiTrip;
import org.jspecify.annotations.NonNull;

public class TestUtils {
    public static final String TRIPS_CSV_FILE_NAME = "trips.csv";

    private static final String OUT_FILE_EXTENSION = ".out";
    private static final String IN_FILE_EXTENSION = ".in";
    private static final String TMP_FILE_NAME = "parquet_trips";
    private static final String TEST_DATA_DIR_NAME = "testData";
    private static final String CSV_DELIMITER = ",";

    @NonNull
    public static Path createTmpParquetFileFromCsv(@NonNull String dirName, @NonNull String fileName)
            throws IOException, URISyntaxException {
        List<TaxiTrip> trips = fromCsvTripData(getTestResourceName(dirName, fileName));

        Path path = createTempParquetFile();
        OutputFile outputFile = HadoopOutputFile.fromPath(
            Utils.toHDFSPath(path), new Configuration()
        );
        Schema schema = createSchema();
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(outputFile)
                                        .withSchema(schema)
                                        .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                                        .build())
        {
            for (TaxiTrip trip : trips) {
                writer.write(createRecord(schema, trip));
            }
        }
        
        return path;
    }

    @NonNull
    public static TestResult fromCsvAvgData(@NonNull String dirName, @NonNull String testName)
            throws URISyntaxException, IOException {
        List<List<String>> csvData = readCsvFromResource(
                getTestResourceName(dirName, testName + OUT_FILE_EXTENSION)
        );
        List<List<String>> times = readCsvFromResource(
                getTestResourceName(dirName, testName + IN_FILE_EXTENSION)
        );
        assert times.size() == 1 : "The times file should have one row";
        List<String> header = times.getFirst();
        assert header.size() == 2 : "There should only be 2 times: pickup time and dropoff time";
        LocalDateTime pickupTime = toLocalDateTime(header.getFirst());
        LocalDateTime dropoffTime = toLocalDateTime(header.getLast());

        Map<Integer, Double> averages = new HashMap<>();
        for (List<String> row : csvData) {
            assert row.size() == 2 : "There should only be 2 columns in a test csv";
            averages.put(Integer.parseInt(row.getFirst()), Double.parseDouble(row.getLast()));
        }
        return new TestResult(pickupTime, dropoffTime, averages);
    }

    @NonNull
    private static List<List<String>> readCsvFromResource(@NonNull String resourceName)
            throws URISyntaxException, IOException {
        URL csv = TestUtils.class.getClassLoader().getResource(resourceName);
        if (csv == null) {
            throw new IllegalStateException("Couldn't find a resource with name: " + resourceName);
        }
        return readCsv(Paths.get(csv.toURI()));
    }

    @NonNull
    private static List<List<String>> readCsv(@NonNull Path path) throws IOException {
        return Files.readAllLines(path)
            .stream()
            .map(line -> Arrays.asList(line.split(CSV_DELIMITER)))
            .collect(Collectors.toList());
    }

    @NonNull
    private static List<TaxiTrip> fromCsvTripData(@NonNull String csvResource) throws URISyntaxException, IOException {
        return fromCsvTripData(readCsvFromResource(csvResource));
    }

    @NonNull
    private static List<TaxiTrip> fromCsvTripData(@NonNull List<List<String>> csvData) {
        List<TaxiTrip> result = new ArrayList<>();
        for (int i = 1; i < csvData.size(); i++) {
            List<String> row = csvData.get(i);
            assert (row.size() == 4) : "There should only be 4 columns in a csv";

            result.add(new TaxiTrip(
                Long.parseLong(row.get(0)),
                Long.parseLong(row.get(1)),
                (int)Double.parseDouble(row.get(2)),
                Double.parseDouble(row.get(3))
            ));
        }
        return result;
    }

    @NonNull
    private static GenericRecord createRecord(@NonNull Schema schema, @NonNull TaxiTrip trip) {
        GenericRecord record = new GenericData.Record(schema);
        record.put(Constants.PICKUP_TIME_COLUMN_NAME, trip.pickupTimeMicros());
        record.put(Constants.DROPOFF_TIME_COLUMN_NAME, trip.dropoffTimeMicros());
        record.put(Constants.PASSENGER_COUNT_COLUMN_NAME, trip.passengerCount());
        record.put(Constants.TRIP_DISTANCE_COLUMN_NAME, trip.tripDistance());
        return record;
    }

    @NonNull
    private static Schema createSchema() {
        return SchemaBuilder.record("TaxiTrip")
            .fields()
            .requiredLong(Constants.PICKUP_TIME_COLUMN_NAME)
            .requiredLong(Constants.DROPOFF_TIME_COLUMN_NAME)
            .requiredDouble(Constants.PASSENGER_COUNT_COLUMN_NAME)
            .requiredDouble(Constants.TRIP_DISTANCE_COLUMN_NAME)
            .endRecord();
    }

    @NonNull
    private static Path createTempParquetFile() throws IOException {
        Path dir = Files.createTempDirectory(null);
        return Files.createTempFile(dir, TMP_FILE_NAME, Constants.PARQUET_FILE_EXTENSION);
    }

    @NonNull
    private static LocalDateTime toLocalDateTime(@NonNull String timeString) {
        return Utils.fromMicros(Long.parseLong(timeString));
    }

    @NonNull
    private static String getTestResourceName(@NonNull String dirName, @NonNull String fileName) {
        return String.format("%s/%s/%s", TEST_DATA_DIR_NAME, dirName, fileName);
    }
}
