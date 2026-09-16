package org.labs;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.max;

public class Kitchen {
    private final AtomicInteger remaining;

    public Kitchen(int foodCount) {
        remaining = new AtomicInteger(foodCount);
    }

    public boolean takeDish() {
        return remaining.getAndUpdate(i -> max(i - 1, 0)) > 0;
    }
}
