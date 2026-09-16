package org.labs;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Spoons {
    private final List<ReentrantLock> spoonLocks;

    public Spoons(int spoonsCount) {
        spoonLocks = Stream.generate(ReentrantLock::new).limit(spoonsCount).toList();
    }

    private void lockSpoonsFor(int programmerId) {
        int firstSpoonId = min(programmerId, (programmerId + 1) % spoonLocks.size());
        int secondSpoonId = max(programmerId, (programmerId + 1) % spoonLocks.size());
        spoonLocks.get(firstSpoonId).lock();
        spoonLocks.get(secondSpoonId).lock();
    }

    private void unlockSpoonsFor(int programmerId) {
        int firstSpoonId = min(programmerId, (programmerId + 1) % spoonLocks.size());
        int secondSpoonId = max(programmerId, (programmerId + 1) % spoonLocks.size());
        spoonLocks.get(secondSpoonId).unlock();
        spoonLocks.get(firstSpoonId).unlock();
    }

    public void withGrabbed(int programmerId, Runnable body) {
        lockSpoonsFor(programmerId);
        try {
            body.run();
        } finally {
            unlockSpoonsFor(programmerId);
        }
    }
}
