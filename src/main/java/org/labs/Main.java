package org.labs;

public class Main {
    public static void main(String[] args) {
        var sim = new Simulation(7, 1_000_000, 100);
        var foodEaten = sim.run();
        int remainingFood = sim.getFoodCount() - foodEaten.stream().mapToInt(Integer::intValue).sum();
        System.out.printf("Remaining food: %d\n", remainingFood);
        for (int i = 0; i < sim.getProgrammersCount(); i++) {
            System.out.printf("Programmer #%d ate %d units of food\n", i, foodEaten.get(i));
        }
    }
}