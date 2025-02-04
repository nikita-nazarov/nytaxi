package nytaxi.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.parquet.io.InputFile;
import org.jspecify.annotations.NonNull;

import nytaxi.calculators.TaxiTrip;

public class Utils {
    @NonNull
    public static List<TaxiTrip> readDataSafe(@NonNull Path dataDir) {
        File[] files = dataDir.toFile().listFiles();
        if (files == null) {
            return List.of();
        }

        List<TaxiTrip> result = new ArrayList<>();
        for (File file : files) {
            Path path = file.toPath();
            if (!path.toString().endsWith(Constants.PARQUET_FILE_EXTENSION)) {
                continue;
            }

            try {
                InputFile inputFile = HadoopInputFile.fromPath(toHDFSPath(path), new Configuration());
                try (ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(inputFile).build()) {
                    GenericRecord record;
                    while ((record = reader.read()) != null) {
                        Long pickupTimeMicros = (Long)record.get(Constants.PICKUP_TIME_COLUMN_NAME);
                        Long dropoffTimeMicros = (Long)record.get(Constants.DROPOFF_TIME_COLUMN_NAME);
                        Double passengerCount = (Double)record.get(Constants.PASSENGER_COUNT_COLUMN_NAME);
                        Double tripDistance = (Double)record.get(Constants.TRIP_DISTANCE_COLUMN_NAME);
                        if (pickupTimeMicros == null || dropoffTimeMicros == null ||
                            passengerCount == null || tripDistance == null) {
                            continue;
                        }
                        var ride = new TaxiTrip(
                            pickupTimeMicros,
                            dropoffTimeMicros,
                            passengerCount.intValue(), 
                            tripDistance
                        );
                        result.add(ride);
                    }
                } catch (IOException ignored) {
                }         
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    public static org.apache.hadoop.fs.Path toHDFSPath(@NonNull Path path) {
        return new org.apache.hadoop.fs.Path(path.toUri());
    }

    @NonNull
    public static LocalDateTime fromMicros(long micros) {
        Instant instant = Instant.ofEpochMilli(micros / 1000);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(0);
        return instant.atOffset(offset).toLocalDateTime();
    }
}
