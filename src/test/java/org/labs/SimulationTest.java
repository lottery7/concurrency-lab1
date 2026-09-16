package org.labs;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationTest {
    private void checkDeviation(Simulation simulation, List<Integer> foodEaten) {
        double mean = (double) simulation.getFoodCount() / simulation.getProgrammersCount();
        foodEaten.forEach(i -> assertTrue(Math.abs(i - mean) / mean < 0.05));
    }

    @Test
    void generalTest() {
        var sim = new Simulation(7, 1_000_000, 2);
        var foodEaten = sim.run();
        checkDeviation(sim, foodEaten);
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
        checkDeviation(sim, foodEaten);
        assertEquals(1_000_000, foodEaten.stream().mapToInt(Integer::intValue).sum());
    }
}