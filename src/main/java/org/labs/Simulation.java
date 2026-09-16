package org.labs;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class Simulation {
    private final int programmersCount;
    private final int foodCount;
    private final int waitersCount;

    public Simulation(int programmersCount, int foodCount, int waitersCount) {
        if (programmersCount <= 0) throw new IllegalArgumentException("programmersCount must be > 0");
        if (foodCount < 0) throw new IllegalArgumentException("foodCount must be >= 0");
        if (waitersCount <= 0) throw new IllegalArgumentException("waitersCount must be > 0");

        this.programmersCount = programmersCount;
        this.foodCount = foodCount;
        this.waitersCount = waitersCount;
    }

    public int getProgrammersCount() {
        return programmersCount;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public List<Integer> run() {
        var kitchen = new Kitchen(foodCount);
        var spoons = new Spoons(programmersCount);
        List<Future<Integer>> futures;
        try (var waiters = new Waiters(waitersCount, kitchen);
             var programmersThreadPool = Executors.newFixedThreadPool(programmersCount)) {
            var programmers = IntStream.range(0, programmersCount)
                    .mapToObj(i -> new Programmer(i, Math.ceilDiv(foodCount, programmersCount), waiters, spoons)
                    ).toList();
            futures = programmers.stream()
                    .map(p -> programmersThreadPool.submit(p::startEating))
                    .toList();
            System.out.println("All programmers started eating, waiting for them...");
        }
        System.out.println("Simulation finished");

        return futures.stream().map(Future::resultNow).toList();
    }


}
