package org.labs;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class Simulation {
    private final int programmersCount;
    private final AtomicInteger remainingFood;
    private final ExecutorService programmersThreadPool;
    private final ExecutorService waitersThreadPool;
    private final List<ReentrantLock> spoonLocks;
    private final CountDownLatch startSimBarrier = new CountDownLatch(1);
    private final CountDownLatch finishSimBarrier;
    private final List<AtomicInteger> foodEaten;

    public Simulation(int programmersCount, int foodCount, int waitersCount) throws InterruptedException {
        if (programmersCount <= 0) throw new IllegalArgumentException("programmersCount must be > 0");
        if (foodCount < 0) throw new IllegalArgumentException("foodCount must be >= 0");
        if (waitersCount <= 0) throw new IllegalArgumentException("waitersCount must be > 0");

        this.programmersCount = programmersCount;
        remainingFood = new AtomicInteger(foodCount);
        foodEaten = Stream.generate(AtomicInteger::new).limit(programmersCount).toList();
        spoonLocks = Stream.generate(ReentrantLock::new).limit(programmersCount).toList();
        finishSimBarrier = new CountDownLatch(programmersCount);

        programmersThreadPool = Executors.newFixedThreadPool(programmersCount);
        waitersThreadPool = Executors.newFixedThreadPool(waitersCount);

        for (int i = 0; i < programmersCount; i++) {
            final int programmerId = i;
            programmersThreadPool.submit(() -> {
                try {
                    startSimBarrier.await();
                    runProgrammer(programmerId);
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishSimBarrier.countDown();
                }
            });
        }

        startSimBarrier.countDown();
        System.out.println("All threads started, waiting for finish...");
        finishSimBarrier.await();
        System.out.println("Simulation finished");

        programmersThreadPool.shutdown();
        waitersThreadPool.shutdown();
    }

    private void runProgrammer(int programmerId) throws ExecutionException, InterruptedException {
        while (true) {
            var foodTakenFuture = waitersThreadPool.submit(this::takeFood);
            if (!foodTakenFuture.get()) break;
            lockSpoonsFor(programmerId);
            try {
                foodEaten.get(programmerId).incrementAndGet();
            } finally {
                unlockSpoonsFor(programmerId);
            }
        }
    }

    private void lockSpoonsFor(int programmerId) {
        int firstSpoonId = Math.min(programmerId, (programmerId + 1) % programmersCount);
        int secondSpoonId = Math.max(programmerId, (programmerId + 1) % programmersCount);
        spoonLocks.get(firstSpoonId).lock();
        spoonLocks.get(secondSpoonId).lock();
    }

    private void unlockSpoonsFor(int programmerId) {
        int firstSpoonId = Math.min(programmerId, (programmerId + 1) % programmersCount);
        int secondSpoonId = Math.max(programmerId, (programmerId + 1) % programmersCount);
        spoonLocks.get(secondSpoonId).unlock();
        spoonLocks.get(firstSpoonId).unlock();
    }

    private boolean takeFood() {
        return remainingFood.decrementAndGet() >= 0;
    }

    public int getRemainingFood() {
        return Math.max(remainingFood.get(), 0);
    }

    public List<Integer> getFoodEaten() {
        return foodEaten.stream().map(AtomicInteger::get).toList();
    }
}
