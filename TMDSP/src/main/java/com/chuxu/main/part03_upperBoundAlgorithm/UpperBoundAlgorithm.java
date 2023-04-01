package com.chuxu.main.part03_upperBoundAlgorithm;

import com.chuxu.entity.CloneObject;
import com.chuxu.entity.Node;
import com.chuxu.main.MainAlgorithm;
import com.chuxu.main.part02_properties.Properties;

import java.util.*;
import java.util.stream.Collectors;

import static com.chuxu.main.MainAlgorithm.*;

public class UpperBoundAlgorithm {

    public static void upperBoundAlgorithm() {
//        System.out.println("============================================================================");
//        System.out.println("============================================================================");
//        System.out.println("现在进入上界子算法");

        //由于可能会重复调用上界子算法，所以上界子算法一开始就要将u置为0然后重新开始
        double cur_u = 0;

        //注意：无论是全局降阶还是局部降阶，都最好克隆之后再进行操作，以免造成一些不必要的麻烦
        CloneObject cloneObject = cloneObject(V, V0, V1);
        LinkedHashSet<Node> V = cloneObject.getV();
        LinkedHashSet<Node> V0 = cloneObject.getV0();
        LinkedHashSet<Node> V1 = cloneObject.getV1();

        //不同于大多数的组合优化问题，在本问题中，当一个结点加入V0或者V1，并不代表其需求就被满足了
        //计算W和R(vi)始终都要从一个【完整】的列表来计算，所以这里再复制一份完整列表备用【浅拷贝，属性一起改】
        LinkedHashSet<Node> W_global = new LinkedHashSet<>();
        for (Node node : W) {
            W_global.add(node.clone());
        }

        //Step1：初始化集合V_temp={}；
        LinkedHashSet<Node> V_temp = new LinkedHashSet<>();

        //Step2：利用性质3找出图G中一定在最小支配阈值集中的结点，并加入集合V1；
        //注意：此时传入性质3的参数为原始图G对应的节点集合V，而不是图G的某个点导出子图对应的节点集合
//        LinkedHashSet<Integer> nodeIdsOfProperty03 = new LinkedHashSet<>();
//        for (Node node : W_global) {
//            if (node.getDegree() == r) {
//                nodeIdsOfProperty03.addAll(node.getNeighborIds());
//                node.setCountsInDominatingSet(r);  //进行全局属性修改
//                node.setRemainCount(0);  //进行全局属性修改
//            }
//        }
        LinkedHashSet<Integer> nodeIdsOfProperty03 = Properties.property02(W_global, MainAlgorithm.V, V0);
        if (!nodeIdsOfProperty03.isEmpty()) {
//            System.out.println("=================================================");
//            System.out.println("nodeIdsOfProperty03 = " + nodeIdsOfProperty03);
            //调用完性质3的后续：①集合更新：V=V\nodeIdsOfProperty03，V1=V1∪nodeIdsOfProperty03，使用stream流过滤的方法进行修改
            for (Integer addId : nodeIdsOfProperty03) {
                V.removeIf(node -> node.getId().equals(addId));
                V1.addAll(VV5.stream().filter(node -> node.getId().equals(addId)).collect(Collectors.toList()));
            }
//            System.out.println("=================================================");
//            System.out.println("进行了集合更新之后的V(V5)：");
//            V.forEach(System.out::println);
//            System.out.println("=================================================");
//            System.out.println("进行了集合更新之后的V1：");
//            V1.forEach(System.out::println);

            //调用完性质3的后续：②去掉已经满足需求的结点，需要重新遍历整个W_global和V1
            //一边遍历W_global，一边删除已经满足需求的结点
            Iterator<Node> iterator = W_global.iterator();
            while (iterator.hasNext()) {
                Node curNode = iterator.next();
                int countInV1 = 0;
                for (Node node : V1) {
                    if (node.getNeighborIds().contains(curNode.getId())) {
                        countInV1++;
                    }
                }
                //如果countInV1>=r，就从W_global把该结点删掉
                if (countInV1 >= r) {
                    iterator.remove();
                } else {
                    //如果countInV1<r，就设置两个属性
                    curNode.setCountsInDominatingSet(countInV1);
                    curNode.setRemainCount(r - countInV1);
                }
            }
//            System.out.println("=================================================:");
//            System.out.println("经过性质3处理后的W_global:");
//            W_global.forEach(System.out::println);
        }

        //Step2结束后，上界子算法中的全局降阶部分也就结束了，可以将相关变量V0,V1,V,【W_global】反拷贝给全局变量了
        //将上界子算法中的V拷贝给主函数中的V
//        System.out.println("=================================================");
//        System.out.println("降阶之后，全局变量反拷贝：");
        reverseCopyGlobalSet(W_global, V, V0, V1);

        //Step3：集合W={vi|vi∊V且|N(G,vi)∩(V1∪Vtemp)|<r}，若|W|=0，上界子算法结束；
        while (true) {
            //计算集合W={vi|vi∊V且|N(G,vi)∩(V1∪Vtemp)|<r}
            LinkedHashSet<Node> V1_Add_Vtemp = new LinkedHashSet<>(V1);
            V1_Add_Vtemp.addAll(V_temp);

            //对于W_global，一遍遍历一边删除已经满足N(G,vi)∩Vtemp|>=r的节点，对于N(G,vi)∩Vtemp|<r的节点也设置好相关属性
            //W_global反正是克隆的，修改了属性也没关系，影响范围不会超出上界子算法
            Iterator<Node> iterator = W_global.iterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();
                int countsInVtemp = 0;
                for (Node node2 : V1_Add_Vtemp) {
                    if (node2.getNeighborIds().contains(node1.getId())) {
                        countsInVtemp++;
                    }
                }
                if (countsInVtemp >= r) {
                    iterator.remove();
                } else {
                    //这些属性每次都是重新计算的新鲜的，重新赋值，不会出现关联影响
                    node1.setCountsInDominatingSet(countsInVtemp);
                    node1.setRemainCount(r - countsInVtemp);
                }
            }

            //若|W_local|=0，上界子算法结束；
            if (W_global.size() == 0) {
                cur_u = V1_Add_Vtemp.size();
                if (cur_u < u) {
                    u = cur_u;
                    S_best = V1_Add_Vtemp;
                }
                return;
            }

            //如果V5中所有节点都加入了Vtemp而W_global中仍然有元素，则该问题无解
            if (V.size() == 0) {
                System.out.println("V5中所有节点都加入了Vtemp而W_global中仍然有元素，则该问题无解！");
                System.exit(0);
            }

            //Step4：找出结点集V5中|N(G,vi)∩W_local|值最大的结点vk，执行Vtemp=Vtemp∪{vk}
            //注意：仅仅是找最大比排序可要方便多了，时间复杂度是O(n)和O(nlogn)的区别，所以第二篇论文的代码有些地方可能写复杂了
            HashMap<Node, Integer> nodeIntegerHashMap = new HashMap<>();
            for (Node node1 : V) {
                int count = 0;
                for (Node node2 : W_global) {
                    if (node1.getNeighborIds().contains(node2.getId())) {
                        count++;
                    }
                }
                nodeIntegerHashMap.put(node1, count);
            }

            //找出|N(G,vi)∩W_local|值最大的结点vk
            Node vk = null;
            int minValue = Integer.MIN_VALUE;
            for (Map.Entry<Node, Integer> nodeIntegerEntry : nodeIntegerHashMap.entrySet()) {
                if (nodeIntegerEntry.getValue() > minValue) {
                    minValue = nodeIntegerEntry.getValue();
                    vk = nodeIntegerEntry.getKey();
                }
            }
//            System.out.println("=================================================");
//            System.out.println("本轮要加入V_temp中的结点为vk = " + vk);
            //执行Vtemp=Vtemp∪{vk}，V=V\{vk}
            V_temp.add(vk);
            V.remove(vk);
        }
    }

    private static void reverseCopyGlobalSet(LinkedHashSet<Node> W_global, LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1) {
//        System.out.println("=================================================");
        MainAlgorithm.W.clear();
        for (Node node : W_global) {
            MainAlgorithm.W.add(node.clone());
        }
//        System.out.println("反拷贝结束后全局变量W：");
//        MainAlgorithm.W.forEach(System.out::println);
//        System.out.println("=================================================");
        MainAlgorithm.V.clear();
        for (Node node : V) {
            MainAlgorithm.V.add(node.clone());
        }
//        System.out.println("反拷贝结束后全局变量V(V5)：");
//        MainAlgorithm.V.forEach(System.out::println);
//        System.out.println("=================================================");
        //将上界子算法中的V1拷贝给主函数中的V1
        MainAlgorithm.V1.clear();
        for (Node node : V1) {
            MainAlgorithm.V1.add(node.clone());
        }
//        System.out.println("反拷贝结束后全局变量V1：");
//        MainAlgorithm.V1.forEach(System.out::println);
//        System.out.println("=================================================");
        //将上界子算法中的F拷贝给主函数中的V0
        MainAlgorithm.V0.clear();
        for (Node node : V0) {
            MainAlgorithm.V0.add(node.clone());
        }
//        System.out.println("反拷贝结束后全局变量V0：");
//        MainAlgorithm.V0.forEach(System.out::println);
    }

    private static CloneObject cloneObject(LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1) {
        LinkedHashSet<Node> V_u = new LinkedHashSet<>();
        for (Node node : V) {
            V_u.add(node.clone());
        }
        LinkedHashSet<Node> V0_u = new LinkedHashSet<>();
        for (Node node : V0) {
            V0_u.add(node.clone());
        }
        LinkedHashSet<Node> V1_u = new LinkedHashSet<>();
        for (Node node : V1) {
            V1_u.add(node.clone());
        }
        return new CloneObject(V_u, V0_u, V1_u);
    }
}