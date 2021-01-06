package dp;

import java.util.Arrays;
import java.util.List;

/**
 * Dynamic Programming about package
 * 动态规划问题是将问题尝试划分成小问题，并一步步将小问题合并成大问题的答案
 *
 * 假如问题中的个数是不定的，那么就不能使用动态规划来进行处理，可使用贪婪算法
 * 例如： 取燕麦、红豆、大米中的各一部分，怎么拿取，那肯定是先拿价值高的
 *
 * 如果各个物品之间存依赖  也不能使用动态规划进行处理
 */
public class Package {

    static Item[] items = new Item[] {
            new Item("吉他", 1500, 1),
            new Item("音响", 3000, 4),
            new Item("笔记本电脑", 2000, 3),
//            new Item("手机", 2000, 1)
    };

    public static void main(String[] args) {
        System.out.println(bestValue(4, Package.items));
        System.out.println();
        System.out.println(bestValue2(4, Package.items));
    }

    private static List<Plan> bestValue(int packageLimit, Item[] choiceAbleItems) {

        Plan[][] plans = new Plan[choiceAbleItems.length][packageLimit];
        for (int i = 0; i < choiceAbleItems.length; i++) {
            Item currentItem = choiceAbleItems[i];
            for (int j = 0; j < packageLimit; j++) {
                int weightLimit = j + 1;
                Plan previousPlan = i == 0 ? new Plan(0) : plans[i - 1][j];
                Plan choicePlan = i != 0 && weightLimit - currentItem.weight > 0
                        ? plans[i - 1][ j - currentItem.weight].addItem(currentItem)
                        : currentItem.weight <= weightLimit ? currentItem.createSelfPlan() : new Plan(0);
                plans[i][j] = previousPlan.value > choicePlan.value ? previousPlan : choicePlan;
            }
        }
//        System.out.println(Arrays.deepToString(plans));
        Arrays.asList(plans)
                .forEach(arr -> {
                    printList(arr);
                    System.out.println();
                });
        System.out.println();
        return Arrays.asList(plans[choiceAbleItems.length - 1]);
    }

    /**
     * 是错误的做法  不能缩减成一行
     * 因为增加物品后，当前的最优配置需要用到前一次的最优配置
     * (也就是已选物品选取的最优解)
     * @param packageLimit
     * @param choiceAbleItems
     * @return
     */
    private static List<Plan> bestValue2(int packageLimit, Item[] choiceAbleItems) {

        Plan[] plans = new Plan[packageLimit];
        for (int i = 0; i < choiceAbleItems.length; i++) {
            Item currentItem = choiceAbleItems[i];
            for (int j = 0; j < packageLimit; j++) {
                int weightLimit = j + 1;
                Plan previousPlan = i == 0 ? new Plan(0) : plans[j];
                Plan choicePlan = i != 0 && weightLimit - currentItem.weight > 0
                        ? plans[ j - currentItem.weight].addItem(currentItem)
                        : currentItem.weight <= weightLimit ? currentItem.createSelfPlan() : new Plan(0);
                plans[j] = previousPlan.value > choicePlan.value ? previousPlan : choicePlan;
            }
        }
//        System.out.println(Arrays.deepToString(plans));
        printList(plans);
        System.out.println();
        return Arrays.asList(plans);
    }

    private static void printList(Plan[] plans) {
        Arrays.asList(plans).forEach(System.out::print);
    }
}
