package org.labs;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Programmer {
    private final int programmerId;
    private final WaitersService waitersService;
    private final SpoonsLock spoonsLock;
    private final Random random = new Random();

    public Programmer(int programmerId, WaitersService waitersService, SpoonsLock spoonsLock) {
        this.programmerId = programmerId;
        this.waitersService = waitersService;
        this.spoonsLock = spoonsLock;
    }

    private void eat() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(random.nextInt(0, 20));
    }

    private void discuss() throws InterruptedException {
        TimeUnit.MICROSECONDS.sleep(random.nextInt(0, 20));
    }

    public int startEating() {
        int foodEaten = 0;
        while (waitersService.serve(programmerId, foodEaten)) {
            try {
                spoonsLock.lock(programmerId);
                try {
                    eat();
                } finally {
                    spoonsLock.unlock(programmerId);
                }
                foodEaten++;
                discuss();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return foodEaten;
    }
}
