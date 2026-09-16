package org.labs;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Programmer {
    private final int programmerId;
    private final int foodQuota;
    private final Waiters waiters;
    private final Spoons spoons;
    private final Random random = new Random();

    public Programmer(int programmerId, int foodQuota, Waiters waiters, Spoons spoons) {
        this.programmerId = programmerId;
        this.foodQuota = foodQuota;
        this.waiters = waiters;
        this.spoons = spoons;

    }

    private void eat() {
        try {
            TimeUnit.MICROSECONDS.sleep(random.nextInt(0, 20));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void discuss() {
        try {
            TimeUnit.MICROSECONDS.sleep(random.nextInt(0, 20));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int startEating() {
        int foodEaten = 0;
        while (foodEaten < foodQuota && waiters.serve(programmerId)) {
            spoons.withGrabbed(programmerId, this::eat);
            foodEaten++;
            discuss();
        }
        return foodEaten;
    }
}
