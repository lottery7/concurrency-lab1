package org.labs;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationTest {
    private void checkDeviation(List<Integer> nums, Double threshold) {
        double average = nums.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        nums.forEach(i -> {
            var dev = Math.abs(i - average) / average;
            assertTrue(dev < threshold, "dev = " + dev + ", expected at max " + threshold);
        });
    }

    @Test
    void generalTest() {
        var sim = new Simulation(7, 1_000_000, 2);
        var foodEaten = sim.run();
        checkDeviation(foodEaten, 0.05);
        assertEquals(1_000_000, foodEaten.stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void zeroFoodTest() {
        var sim = new Simulation(7, 0, 2);
        var foodEaten = sim.run();
        foodEaten.forEach(i -> assertEquals(0, i));
        assertEquals(0, foodEaten.stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void foodLessThanProgrammersTest() {
        var sim = new Simulation(7, 3, 2);
        var foodEaten = sim.run();
        assertEquals(3, foodEaten.stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void manyWaitersTest() {
        var sim = new Simulation(7, 1_000_000, 100);
        var foodEaten = sim.run();
        assertEquals(1_000_000, foodEaten.stream().mapToInt(Integer::intValue).sum());
//        checkDeviation(foodEaten, 0.25);
    }
}