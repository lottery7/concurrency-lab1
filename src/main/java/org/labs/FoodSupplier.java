package org.labs;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

public class FoodSupplier {
    private final AtomicInteger remaining;

    public FoodSupplier(int foodCount) {
        remaining = new AtomicInteger(foodCount);
    }

    public boolean tryTake() {
        return remaining.getAndUpdate(i -> max(i - 1, 0)) > 0;
    }
}
