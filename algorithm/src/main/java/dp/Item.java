package dp;

public class Item {

    final String name;
    final int price;
    final int weight;

    public Item(String name, Integer price, int weight) {
        this.name = name;
        this.price = price;
        this.weight = weight;
    }

    public Plan createSelfPlan() {
        Plan plan = new Plan(this.price);
        plan.items.add(this);
        return plan;
    }
}
