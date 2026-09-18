package org.labs;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class WaitersService implements AutoCloseable {
    private final List<Thread> waiters;
    private final FoodSupplier foodSupplier;
    private final PriorityBlockingQueue<ServeRequest> requestsQueue;

    public WaitersService(int waitersCount, FoodSupplier foodSupplier) {
        this.foodSupplier = foodSupplier;
        requestsQueue = new PriorityBlockingQueue<>(11, Comparator.comparingInt(sr -> sr.priority));
        waiters = Stream.generate(() -> Thread.startVirtualThread(this::waiterLoop)).limit(waitersCount).toList();
    }

    private void waiterLoop() {
        while (true) {
            ServeRequest request = null;
            try {
                request = requestsQueue.take();
                processServeRequest(request);
            } catch (InterruptedException e) {
                if (request != null) {
                    request.result.completeExceptionally(e);
                }
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processServeRequest(ServeRequest request) throws InterruptedException {
        int distance = request.programmerId * 10;
        waiterWalk(distance);
        var isFoodTaken = foodSupplier.tryTake();
        waiterWalk(distance);
        request.result.complete(isFoodTaken);
    }

    private void waiterWalk(int distance) throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(distance);
    }

    public boolean serve(int programmerId, int priority) {
        try {
            var request = new ServeRequest(programmerId, priority, new CompletableFuture<>());
            requestsQueue.add(request);
            return request.result.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (ExecutionException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        waiters.forEach(Thread::interrupt);
        requestsQueue.forEach(r -> r.result.completeExceptionally(new InterruptedException()));
    }

    private record ServeRequest(
            int programmerId,
            int priority,
            CompletableFuture<Boolean> result
    ) {
    }
}
