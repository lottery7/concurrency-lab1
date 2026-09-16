package org.labs;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Waiters implements AutoCloseable {
    private final ExecutorService threadPool;
    private final Kitchen kitchen;

    public Waiters(int waitersCount, Kitchen kitchen) {
        threadPool = Executors.newFixedThreadPool(waitersCount);
        this.kitchen = kitchen;
    }

    private void walk(int distance) {
        try {
            TimeUnit.MICROSECONDS.sleep(distance);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean serveByWaiter(int programmerId) {
        int distance = programmerId * 10;
        walk(distance);
        var dishTaken = kitchen.takeDish();
        walk(distance);
        return dishTaken;
    }

    public boolean serve(int programmerId) {
        try {
            return threadPool.submit(() -> serveByWaiter(programmerId)).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void close() {
        threadPool.shutdown();
    }
}
