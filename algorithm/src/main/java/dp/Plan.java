package dp;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Plan {

    ArrayList<Item> items = new ArrayList<>();
    final int value;

    public Plan(int value) {
        this.value = value;
    }


    public static Plan mergePlan(Plan planA, Plan planB) {
        Plan newPlan = new Plan(planA.value + planB.value);
        newPlan.items.addAll(new ArrayList<>(planA.items));
        newPlan.items.addAll(new ArrayList<>(planB.items));
        return newPlan;
    }

    public Plan addItem(Item item) {
        Plan newPlan = new Plan(this.value + item.price);
        newPlan.items = new ArrayList<>(this.items);
        newPlan.items.add(item);
        return newPlan;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "items=" + items.stream().map(i -> i.name).collect(Collectors.joining(",")) +
                ", value=" + value +
                '}';
    }
}
