package nytaxi.calculators;

import org.jspecify.annotations.NonNull;

public record TaxiTrip(
    long pickupTimeMicros,
    long dropoffTimeMicros,
    int passengerCount, 
    double tripDistance
) implements Comparable<TaxiTrip> {
    public boolean isValid(long pickupTimeMicros, long dropoffTimeMicros) {
        return this.pickupTimeMicros >= pickupTimeMicros && this.dropoffTimeMicros <= dropoffTimeMicros;
    }

    @Override
    public int compareTo(@NonNull TaxiTrip other) {
        return Long.compare(pickupTimeMicros, other.pickupTimeMicros);
    }
}
