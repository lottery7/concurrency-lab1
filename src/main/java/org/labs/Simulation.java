package org.labs;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Phaser;
import java.util.concurrent.StructuredTaskScope;
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
        var foodSupplier = new FoodSupplier(foodCount);
        var spoonsLock = new SpoonsLock(programmersCount);

        try (var waitersService = new WaitersService(waitersCount, foodSupplier);
             var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var programmers = IntStream.range(0, programmersCount)
                    .mapToObj(i -> new Programmer(i, waitersService, spoonsLock))
                    .toList();

            var phaser = new Phaser(programmersCount + 1);
            var subtasks = programmers.stream()
                    .map(programmer -> scope.fork(() -> {
                        phaser.arriveAndAwaitAdvance();
                        return programmer.startEating();
                    }))
                    .toList();

            phaser.arriveAndAwaitAdvance();
            System.out.println("All programmers started eating, waiting for them...");

            scope.join();
            scope.throwIfFailed();
            System.out.println("Simulation finished");

            return subtasks.stream().map(StructuredTaskScope.Subtask::get).toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulation interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Simulation execution failed", e);
        }
    }
}
