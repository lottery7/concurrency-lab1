package org.labs;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class SpoonsLock {
    private final List<ReentrantLock> spoonLocks;

    public SpoonsLock(int spoonsCount) {
        spoonLocks = Stream.generate(ReentrantLock::new).limit(spoonsCount).toList();
    }

    void lock(int programmerId) {
        int firstSpoonId = min(programmerId, (programmerId + 1) % spoonLocks.size());
        int secondSpoonId = max(programmerId, (programmerId + 1) % spoonLocks.size());
        spoonLocks.get(firstSpoonId).lock();
        spoonLocks.get(secondSpoonId).lock();
    }

    void unlock(int programmerId) {
        int firstSpoonId = min(programmerId, (programmerId + 1) % spoonLocks.size());
        int secondSpoonId = max(programmerId, (programmerId + 1) % spoonLocks.size());
        spoonLocks.get(secondSpoonId).unlock();
        spoonLocks.get(firstSpoonId).unlock();
    }
}
