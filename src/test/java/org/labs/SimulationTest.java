package org.labs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SimulationTest {
    private void checkDeviation(int programmersCount, int foodCount, List<Integer> foodEaten) {
        int mean = foodCount / programmersCount;
        foodEaten.forEach(
                i -> Assertions.assertTrue((double) Math.abs(i - mean) / foodCount < 0.05)
        );
    }

    @Test
    void generalTest() throws Exception {
        var sim = new Simulation(7, 1_000_000, 2);
        checkDeviation(7, 1_000_000, sim.getFoodEaten());
        Assertions.assertEquals(1_000_000, sim.getFoodEaten().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void zeroFoodTest() throws Exception {
        var sim = new Simulation(7, 0, 2);
        sim.getFoodEaten().forEach(i -> Assertions.assertEquals(0, i));
        Assertions.assertEquals(0, sim.getFoodEaten().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void foodLessThanProgrammersTest() throws Exception {
        var sim = new Simulation(7, 3, 2);
        Assertions.assertEquals(3, sim.getFoodEaten().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void manyWaitersTest() throws Exception {
        var sim = new Simulation(7, 1_000_000, 100);
        checkDeviation(7, 1_000_000, sim.getFoodEaten());
        Assertions.assertEquals(1_000_000, sim.getFoodEaten().stream().mapToInt(Integer::intValue).sum());
    }
}