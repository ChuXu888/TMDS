package com.chuxu.main.part07_comparedAlgorithm;

import com.chuxu.entity.Node;
import com.chuxu.entity.PriorityQueueNode;
import com.chuxu.main.part01_preHandledData.r_equals_3.PreHandleData_n50_1;

import java.util.*;

public class BranchAndBoundAlgorithm {
    private static final int[][] neighborMatrix = PreHandleData_n50_1.neighborMatrix;  //邻接矩阵
    private static final int r = PreHandleData_n50_1.r;  //阈值
    private static final List<Node> V = new ArrayList<>();  //结点列表
    private static double upBound;  //全局下界【最优值】
    private static final LinkedHashSet<Node> S_best = new LinkedHashSet<>();  //最优解
    private static int count = 0;  //统计while循环执行了多少次，也即访问了多少个结点

    public static void main(String[] args) {
        //记录程序开始时间
        long start = System.currentTimeMillis();

        //1.数据初始化
        PreHandleData_n50_1.preHandle();
        deepCopy();

        //2.使用跟回溯算法同样的思路来计算上界，但是不使用数学性质
        upperBoundAlgorithm();
        System.out.println("初始上界【贪心求得的可行解】 = " + upBound);
        S_best.forEach(System.out::println);

        //3.调用分支定界算法
        branchAndBoundAlgorithm();
        //记录程序结束时间
        long end = System.currentTimeMillis();

        //4.打印结果
        System.out.println("===========================================");
        System.out.println("总结点搜索次数 = " + count);
        System.out.println("最优值 = " + upBound);
//        System.out.println("最优解 = ");
//        S_best.forEach(System.out::println);
        System.out.println("程序运行总时间为：" + (end - start) / 1000.0);
    }

    public static void branchAndBoundAlgorithm() {
        //定义优先队列
//        Queue<PriorityQueueNode> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(PriorityQueueNode::getLowerBound));
        Queue<PriorityQueueNode> priorityQueue = new LinkedList<>();
        //计算根节点的下界，然后将根节点加入队列
        PriorityQueueNode root = new PriorityQueueNode(new LinkedHashSet<>(), new LinkedHashSet<>(), V, null);
        root.setLowerBound((double) lowerBoundAlgorithm(root));
        priorityQueue.add(root);
        //循环直至队列为空
        while (!priorityQueue.isEmpty()) {
            count++;
//            System.out.println("count = " + count);
            //弹出队列中上界最大的结点
            PriorityQueueNode curQueueNode = priorityQueue.poll();
//            System.out.println("curQueueNode = " + curQueueNode);
            LinkedHashSet<Node> curVV1 = curQueueNode.getVV1();
            LinkedHashSet<Node> curVV0 = curQueueNode.getVV0();
            List<Node> remainV = curQueueNode.getRemainV();
            //如果remainV中已经为空了，说明当前结点是一个叶子节点，对应的解要么不是解，要么就是一个可行解
            if (remainV.isEmpty()) {
                //判断当前是否为一个可行解，也即计算一次W看是否为空
                LinkedHashSet<Node> W = new LinkedHashSet<>();
                for (Node node : V) W.add(node.clone());
                calculateW(curVV1, W);
                if (W.isEmpty()) {
                    //是一个可行解
                    if (curVV1.size() < upBound) {
                        upBound = curVV1.size();
                        System.out.println("更新上界 = " + upBound);
                        S_best.clear();
                        S_best.addAll(curVV1);
                        //还要判断队列中剩下的结点的下界是不是大于当前已经更新了的上界值，如果大于就可以去掉了
                        //因为它们的下限都比这个可行解的目标值大，再继续搜索下去也搜不出什么名堂
                        priorityQueue.removeIf(nextNode -> nextNode.getLowerBound() >= upBound);
                    }
                }
            } else {
                //注意：new LinkedHashSet<>(curVV1)和new LinkedHashSet<>(curVV0)和new ArrayList<>(remainV)，
                //否则下面的变动会导致队列中的结点的某些属性跟着变，影响队列里的结点
                List<Node> newRemainV = new ArrayList<>(remainV);
                Node curNode = newRemainV.get(0);
                newRemainV.remove(curNode);
                //1.将curNode加入VV1得到左子节点
                LinkedHashSet<Node> newVV1 = new LinkedHashSet<>(curVV1);
                newVV1.add(curNode);
                PriorityQueueNode leftNode = new PriorityQueueNode(newVV1, curVV0, newRemainV, null);
                double lowerBound = lowerBoundAlgorithm(leftNode);
                leftNode.setLowerBound(lowerBound);
                priorityQueue.offer(leftNode);
                //2.将curNode加入VV0得到右子节点
                //住：参考论文中已经删去的性质2，现在有用处了：将所有节点去掉VV0之后，再来计算一遍W，
                //如果计算完W中仍有节点，此时无解，返回一个惩罚值。降阶回溯算法是因为性质3的存在使得原性质2失去作用
                //其实这个条件最好放在分支定界算法中
                LinkedHashSet<Node> newVV0 = new LinkedHashSet<>(curVV0);
                newVV0.add(curNode);
                PriorityQueueNode rightNode = new PriorityQueueNode(curVV1, newVV0, newRemainV, null);
                if (judgeFeasible(rightNode)) {
                    //若返回true，才计算当前结点的下界，并将其加入队列中
                    lowerBound = lowerBoundAlgorithm(rightNode);
                    rightNode.setLowerBound(lowerBound);
                    priorityQueue.offer(rightNode);
                }
            }
        }
    }

    //将计算W的代码提取为一个方法：上下界算法，分支定界算法中都用得到
    private static void calculateW(LinkedHashSet<Node> VV1, LinkedHashSet<Node> W) {
        Iterator<Node> iterator = W.iterator();
        while (iterator.hasNext()) {
            Node curNode = iterator.next();
            int countsInVtemp = 0;
            for (Node node : VV1) {
                if (node.getNeighborIds().contains(curNode.getId())) {
                    countsInVtemp++;
                }
            }
            if (countsInVtemp >= r) {
                iterator.remove();
            } else {
                //这些属性每次都是重新计算的新鲜的，重新赋值，不会出现关联影响
                curNode.setCountsInDominatingSet(countsInVtemp);
                curNode.setRemainCount(r - countsInVtemp);
            }
        }
    }

    //下界算法
    public static int lowerBoundAlgorithm(PriorityQueueNode priorityQueueNode) {
        //当已经某个节点的左右子节点的remainV已经为空时，也即此时已经到达了一个叶子节点，
        //将阈值支配集中结点的个数返回作为上界【可行解对应的目标值】
        List<Node> remainV = priorityQueueNode.getRemainV();
        if (remainV.isEmpty()) {
            return priorityQueueNode.getVV1().size();
        }
        //取出当前结点的相关属性
        LinkedHashSet<Node> curVV1 = priorityQueueNode.getVV1();
        //Step1：初始化集合V_temp=VV1，初始化b=|VV1)|；初始化W=V
        LinkedHashSet<Node> V_temp = new LinkedHashSet<>(curVV1);
        int b = V_temp.size();
        LinkedHashSet<Node> W = new LinkedHashSet<>();
        for (Node node : V) W.add(node.clone());
        //while循环
        while (true) {
            //Step2：计算集合W={vi|vi∊V且|N(G,vi)∩Vtemp|<r}，若|W|=0，转至Step4；否则∀vi∊W，计算R(vi)=r-|N(G,vi)∩Vtemp)|；
            calculateW(V_temp, W);
            //若|W|=0，转至Step4；
            if (W.isEmpty()) {
                break;
            }
            //Step3：将W中结点按照R(vi)的值从大到小排序，假设vk表示排序后W中的第一个结点；
            Node vk = null;
            int maxValue = -1;
            for (Node node : W) {
                if (node.getRemainCount() > maxValue) {
                    maxValue = node.getRemainCount();
                    vk = node;
                }
            }
            //循环结束后，就找到了W中R(vi)的值最大的结点vk，
            //执行b=b+R(vk)，Vtemp=Vtemp∪N(G[VV5],vk)；转至Step2；
            b += vk.getRemainCount();
            for (Integer neighborId : vk.getNeighborIds()) {
                //根据id从remainV中找结点对象
//                V_temp.addAll(remainV.stream().filter(node -> Objects.equals(node.getId(), neighborId)).collect(Collectors.toList()));
                for (Node node : remainV) {
                    if (node.getId().equals(neighborId)) {
                        V_temp.add(node);
                    }
                }
            }
        }
        //Step4：由性质4，b=max{b,r+1}
        return Math.max(b, r + 1);
    }

    //原性质2，用于右子节点【将结点加入VV0中时】判断是否无解
    private static boolean judgeFeasible(PriorityQueueNode priorityQueueNode) {
        //准备素材，potentialNodes应包含VV1，V\VV0=VV1∪RemainV
//        System.out.println("priorityQueueNode.getRemainV().size() = " + priorityQueueNode.getRemainV().size());
        //这里要新建列表，否则会出现原remainV跟着修改
        List<Node> potentialNodes = new ArrayList<>(priorityQueueNode.getRemainV());
        potentialNodes.addAll(priorityQueueNode.getVV1());
//        System.out.println("priorityQueueNode.getRemainV().size() = " + priorityQueueNode.getRemainV().size());
        LinkedHashSet<Node> W = new LinkedHashSet<>();
        for (Node node : V) W.add(node.clone());
        //循环遍历
        for (Node node1 : W) {
            int countsInPotentialSet = 0;
            for (Node node2 : potentialNodes) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    countsInPotentialSet++;
                }
            }
            if (countsInPotentialSet < r) {
                return false;
            }
        }
        //如果程序能执行到这里，说明每个W中的节点都至少有r个邻接结点在remainV中，就返回true。
        //当为叶子节点时，VV5(V)={}
        return true;
    }

    //上界算法
    public static void upperBoundAlgorithm() {
        //将V复制一份进行操作
        List<Node> V_copy = new ArrayList<>();
        for (Node node : V) V_copy.add(node.clone());
        //定义V_temp和W两个变量，V_temp初始化为空，W初始化为V
        LinkedHashSet<Node> V_temp = new LinkedHashSet<>();
        LinkedHashSet<Node> W = new LinkedHashSet<>(V_copy);
        //借用降阶回溯算法的思路：不断从还没加入V_temp的结点中，寻找能覆盖W中最多结点的那个节点，将其加入V_temp
        while (true) {
            //计算本轮W
            calculateW(V_temp, W);
            //打印本轮W
            System.out.println("============================================");
            System.out.println("本轮W：");
            W.forEach(System.out::println);
            //如果W为空则退出循环
            if (W.isEmpty()) {
                upBound = V_temp.size();
                S_best.addAll(V_temp);
                return;
            }
            //找出结点集V_copy\V_temp【实时更新，遍历V_copy即可】中|N(G,vi)∩W|值最大的结点vk，
            //执行V_temp=V_temp∪{vk}，V5= V5\{vk}
            Node vk = null;
            int maxValue = -1;
            for (Node node : V_copy) {
                int countOfCoverW = 0;
                for (Node node1 : W) {
                    if (node1.getNeighborIds().contains(node.getId())) {
                        countOfCoverW++;
                    }
                }
                if (countOfCoverW > maxValue) {
                    maxValue = countOfCoverW;
                    vk = node;
                }
            }
            //将vk加入V_temp，同时从V_copy中移除vk
            System.out.println("============================================");
            System.out.println("本轮vk = " + vk);
            V_temp.add(vk);
            V_copy.remove(vk);
        }
    }

    public static void deepCopy() {
        LinkedHashSet<Node> nodes = PreHandleData_n50_1.nodes;
        for (Node node : nodes) {
            V.add(node.clone());
        }
    }
}
