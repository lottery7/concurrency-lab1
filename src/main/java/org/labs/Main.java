package org.labs;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var sim = new Simulation(7, 1_000_000, 2);
        System.out.printf("Remaining food: %d\n", sim.getRemainingFood());
        var foodEaten = sim.getFoodEaten();
        for (int i = 0; i < 7; i++) {
            System.out.printf("Programmer #%d ate %d units of food\n", i, foodEaten.get(i));
        }
    }
}