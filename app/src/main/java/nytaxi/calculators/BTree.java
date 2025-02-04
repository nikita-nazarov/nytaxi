package nytaxi.calculators;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BTree {
    private static final int B = 32;
    private final int blocksNum;
    private final TimeToIndex[] bTree;

    private record TimeToIndex(long time, int index){}

    public BTree(@NonNull List<TaxiTrip> trips) {
        List<TimeToIndex> timeToIndices = new ArrayList<>(trips.size());
        for (int i = 0; i < trips.size(); i++) {
            timeToIndices.add(new TimeToIndex(trips.get(i).pickupTimeMicros(), i));
        }
        blocksNum = (timeToIndices.size() + B - 1) / B;
        bTree = new TimeToIndex[blocksNum * B];
        build(0,  timeToIndices.iterator());
    }

    /*
     * Returns a lower bound of a query if exists, otherwise -1 if
     * all elements are less than query.
     */
    public int lowerBound(long query) {
        int index = lowerBoundImpl(query, 0, -1);
        if (index == -1) {
            return -1;
        }
        return bTree[index].index;
    }

    private int lowerBoundImpl(long query, int block, int prevIndex) {
        if (block >= blocksNum) {
            return prevIndex;
        }

        int startIndex = block * B;
        int index = getIndexOfFirstGreaterOrEqualElement(startIndex, query);
        int newPrevIndex = startIndex + index;
        if (index == B) {
            return lowerBoundImpl(query, child(block, index), prevIndex);
        }
        return lowerBoundImpl(query, child(block, index), newPrevIndex);
    }

    private int getIndexOfFirstGreaterOrEqualElement(int startIndex, long query) {
        for (int i = 0; i < B; i++) {
            long value = bTree[startIndex + i].time;
            if (value >= query) {
                return i;
            }
        }
        return B;
    }

    private void build(int block, Iterator<TimeToIndex> it) {
        if (block >= blocksNum) {
            return;
        }

        for (int i = 0; i < B; i++) {
            build(child(block, i), it);
            int index = B * block + i;
            if (it.hasNext()) {
                bTree[index] = it.next();
            } else {
                bTree[index] = new TimeToIndex(Long.MAX_VALUE, -1);
            }
        }
        build(child(block, B), it);
    }

    private int child(int block, int index) {
        return block * (B + 1) + index + 1;
    }
}
