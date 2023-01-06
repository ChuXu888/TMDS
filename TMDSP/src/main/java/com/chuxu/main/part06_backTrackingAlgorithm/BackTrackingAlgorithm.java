package com.chuxu.main.part06_backTrackingAlgorithm;

import com.chuxu.entity.Node;
import com.chuxu.main.part02_properties.Properties;
import com.chuxu.main.part04_lowerBoundAlgorithm.LowerBoundAlgorithm;

import java.util.*;
import java.util.stream.Collectors;

import static com.chuxu.main.MainAlgorithm.*;

public class BackTrackingAlgorithm {

    public static void backTrackingAlgorithm() {
        System.out.println("============================================================================");
        System.out.println("============================================================================");
        System.out.println("现在进入回溯子算法");
        System.out.println("======================================================");
        System.out.println("W：");
        W.forEach(System.out::println);
        System.out.println("V(VV5)：");
        V.forEach(System.out::println);

        backTracking();
    }

    public static void backTracking() {
        //二叉树搜索次数
        count++;

        //Step1：当|VV5|=0时，此时所有的结点都确定下来了开或者不开，才形成了一个完整的方案，才是叶子节点
        if (V_list.size() == 0) {
            System.out.println("这里是一个叶子节点！！！");
            //叶子节点搜索次数
            countOfLeaf++;
            //若|W|=0，则得到可行解Sk=(V1∪VV1)==>通过性质2判断是否W中的【每个结点】均有至少r个节点位于V1∪VV1中
            //因为性质2是只要W中有一个结点在VV5_Add_V1_Add_VV1【由if可知，VV5此时为空，所以就是V1∪VV1了】中的邻接点数小于r就返回false，
            //所以如果返回了true，就说明W中所有结点在VV5_Add_V1_Add_VV1中的邻接点数>=r
            //计算结束后，如果feasibleSolutionFlag仍为true，说明当前V1∪VV1是一个可行解
            if (Properties.property02(W, V, V1, VV1)) {
                LinkedHashSet<Node> V1_Add_VV1 = new LinkedHashSet<>(V1);
                V1_Add_VV1.addAll(VV1);
                int z = V1_Add_VV1.size();
                if (z < u) {
                    u = z;
                    S_best.clear();
                    S_best.addAll(V1_Add_VV1);
                }
            }
            //到达叶子节点是肯定要return的
            return;
        }

        //Step2：情况(1)：假设VV5中第一个结点vi不在最小支配阈值集中，VV0=VV0∪{vi}，VV5=VV5\{vi}；
        //对V_stack和V【同时做更新】，因为性质里面形参都是LinkedHashSet<Node>，用V_stack不方便；
        //而且如果没有V_stack弹出的这个节点，V还不好删除这个节点，因为即使LinkedHashSet其实也是没有索引的，需要根据curNode.getId()来删除
        System.out.println("现在进入右子树假设！");
        Node curNode = V_list.get(0);
        System.out.println("curNode = " + curNode);

        VV0.add(curNode);
        V.remove(curNode);
        V_list.remove(curNode);
        //做完左假设之后要更新W，做完右假设之后可以不更新

        //由性质4确定的加入VV1中的结点集
        LinkedHashSet<Node> V_temp = new LinkedHashSet<>();
        //W的备份，一旦性质4满足，并且达到了降阶效果，那么回溯时则需要将W回溯到满足性质4之前的状态
        LinkedHashSet<Node> W_bak_right = new LinkedHashSet<>(W);

        //判断此时是否满足性质2的条件，若满足则该情况下无可行解，右子树剪枝，转至Step3
        //若返回false，则无可行解，右子树剪枝，若返回true则继续
        if (Properties.property02(W, V, V1, VV1)) {
            //如果满足性质10的条件，走一个分支；如果不满足，走另一个分支
            LinkedHashSet<Node> Sc = Properties.property10_2(W, V);
            if (Sc.size() != 0) {
                //若满足性质10，也是得到了一个完整方案，也是一个叶子节点
                countOfLeaf++;
                countOfProperty10++;

                LinkedHashSet<Node> Sc_Add_V1_Add_VV1 = new LinkedHashSet<>(Sc);
                Sc_Add_V1_Add_VV1.addAll(V1);
                Sc_Add_V1_Add_VV1.addAll(VV1);
                System.out.println("满足性质10的条件，Sc∪V1∪VV1即为一个可行解！");
                Sc_Add_V1_Add_VV1.forEach(System.out::println);
                int z = Sc_Add_V1_Add_VV1.size();
                if (z < u) {
                    u = z;
                    S_best.clear();
                    S_best.addAll(Sc_Add_V1_Add_VV1);
                }
                //性质10其实也是确定下来了每个节点的身份，所以也是一个叶子节点，应该也是return，而不是继续回溯【另一个代码可能写错了】
                //这里不应该用return，因为return直接就返回上一层了，但是这里右子树如果直接得到了可行解，仍然要继续搜索左子树的
                //所以这里应该什么都不做，让其自然地去继续搜索即可
//                return;
            } else {
                int b = LowerBoundAlgorithm.lowerBoundAlgorithm(V, V0, V1, VV0, VV1);
                if (b <= u) {
                    //由性质4得出加入VV1中的结点集为Vtemp，执行VV1=VV1∪Vtemp，VV5=VV5\Vtemp
                    LinkedHashSet<Integer> nodeIdsOfProperty04 = Properties.property04(W, V, V1, VV1);
                    //如果性质4满足，那就有一套相应的操作。
                    if (nodeIdsOfProperty04.size() != 0) {
                        System.out.println("=================================================");
                        System.out.println("nodeIdsOfProperty04 = " + nodeIdsOfProperty04);
                        //调用完性质4的后续：①集合更新：V=V\nodeIdsOfProperty04，VV1=VV1∪nodeIdsOfProperty04，使用stream流过滤的方法进行修改
                        for (Integer addId : nodeIdsOfProperty04) {
                            V_temp.addAll(VV5.stream().filter(node -> node.getId().equals(addId)).collect(Collectors.toList()));
                        }
                        //使用Stream流过滤出V_temp中属于VV5的节点；或者用交集retainAll()应该也是可以的
                        Set<Node> nodes = V_temp.stream().filter(item -> V.contains(item)).collect(Collectors.toSet());
                        V_temp .clear();
                        V_temp.addAll(nodes);
                        //还是那个问题，Vtemp要含有VV5(V)中的节点，才是真正达到了降阶的效果
                        if (!V_temp.isEmpty()) {
                            System.out.println("=================================================");
                            System.out.println("Vtemp要含有VV5(V)中的节点，达到了降阶效果");
                            countOfProperty04++;

                            V_temp.forEach(System.out::println);
                            //要同时更新V和V_stack
                            V.removeAll(V_temp);
                            V_list.removeAll(V_temp);
                            VV1.addAll(V_temp);
                            System.out.println("=================================================");
                            System.out.println("进行了集合更新之后的V(VV5)：");
                            V.forEach(System.out::println);
                            System.out.println("进行了集合更新之后的V_list：");
                            V_list.forEach(System.out::println);
                            System.out.println("=================================================");
                            System.out.println("进行了集合更新之后的VV1：");
                            VV1.forEach(System.out::println);

                            //调用完性质4的后续：②去掉已经满足需求的结点，需要重新遍历整个W和V1∪VV1
                            //一边遍历W，一边删除已经满足需求的结点
                            LinkedHashSet<Node> V1_Add_VV1 = new LinkedHashSet<>(V1);
                            V1_Add_VV1.addAll(VV1);
                            Iterator<Node> iterator = W.iterator();
                            while (iterator.hasNext()) {
                                Node node1 = iterator.next();
                                int countInV1AndVV1 = 0;
                                for (Node node2 : V1_Add_VV1) {
                                    if (node2.getNeighborIds().contains(node1.getId())) {
                                        countInV1AndVV1++;
                                    }
                                }
                                //如果countInV1>=r，就从W_global把该结点删掉
                                if (countInV1AndVV1 >= r) {
                                    iterator.remove();
                                } else {
                                    //如果countInV1<r，就设置两个属性
                                    node1.setCountsInDominatingSet(countInV1AndVV1);
                                    node1.setRemainCount(r - countInV1AndVV1);
                                }
                            }
                            System.out.println("=================================================:");
                            System.out.println("经过性质4处理后的W:");
                            W.forEach(System.out::println);
                        }
                    }
                    backTracking();
                }
            }
        }

        //Step3：返回上一层前执行VV0=VV0\{vi}，VV1=VV1\Vtemp，VV5=VV5∪{vi}∪Vtemp，还有W的状态
        //把这部分放前面，然后push(curNode)，从而保证curNode在栈顶
        if (!V_temp.isEmpty()) {
            V_list.addAll(V_temp);
            V.addAll(V_temp);
            VV1.removeAll(V_temp);
            W.clear();
            W.addAll(W_bak_right);
        }

        VV0.remove(curNode);
        V.add(curNode);
        V_list.add(curNode);

        Collections.sort(V_list);
        System.out.println("恢复状态并重新按照序号排序完毕后V_list的状态：");
        V_list.forEach(System.out::println);

        //Step4：情况(2)：假设VV5中第一个结点vi在最小支配阈值集中，VV1=VV1∪{vi}，VV5=VV5\{vi}
        System.out.println("现在进入左子树假设！");
        VV1.add(curNode);
        V.remove(curNode);
        V_list.remove(curNode);
        //由于左子树是假设在最小支配阈值集中，所以要更新W

        //此时若|V1∪VV1|≥u，则基于当前情况后续求得的解不会优于当前上界，左子树剪枝，转至Step5；
        LinkedHashSet<Node> V1_Add_VV1 = new LinkedHashSet<>(V1);
        V1_Add_VV1.addAll(VV1);
        //W的备份，左子树假设某个节点在最小支配阈值集中，要更新W
        LinkedHashSet<Node> W_bak_left = new LinkedHashSet<>(W);

        if (V1_Add_VV1.size() < u) {
            //如果|V1∪VV1|≥u直接左子树不搜索了，直接回溯，那么W也就无需更新；而如果|V1∪VV1|<u，那么第一件事就是要更新W
            //一边遍历W，一边删除已经满足需求的结点
            Iterator<Node> iterator = W.iterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();
                int countInV1AndVV1 = 0;
                for (Node node2 : V1_Add_VV1) {
                    if (node2.getNeighborIds().contains(node1.getId())) {
                        countInV1AndVV1++;
                    }
                }
                //如果countInV1>=r，就从W_global把该结点删掉
                if (countInV1AndVV1 >= r) {
                    iterator.remove();
                } else {
                    //如果countInV1<r，就设置两个属性
                    node1.setCountsInDominatingSet(countInV1AndVV1);
                    node1.setRemainCount(r - countInV1AndVV1);
                }
            }
            System.out.println("=================================================:");
            System.out.println("左子树中假设某个节点在最小支配阈值集中，然后更新W:");
            W.forEach(System.out::println);

            //判断此时的无向图G=(V,E)是否满足性质10的条件，若满足则得到可行解Sk=(V1∪VV1)，
            //计算Sk的目标函数值z=|Sk|，若z<u则更新上界u=z，Sbest =Sk，返回上一层；
            //如果满足性质10的条件，走一个分支；如果不满足，走另一个分支
            LinkedHashSet<Node> Sc = Properties.property10_2(W, V);
            if (Sc.size() != 0) {
                //若满足性质10，也是得到了一个完整方案，也是一个叶子节点
                countOfLeaf++;
                countOfProperty10++;

                LinkedHashSet<Node> Sc_Add_V1_Add_VV1 = new LinkedHashSet<>(Sc);
                Sc_Add_V1_Add_VV1.addAll(V1);
                Sc_Add_V1_Add_VV1.addAll(VV1);
                System.out.println("满足性质10的条件，Sc∪V1∪VV1即为一个可行解！");
                Sc_Add_V1_Add_VV1.forEach(System.out::println);
                int z = Sc_Add_V1_Add_VV1.size();
                if (z < u) {
                    u = z;
                    S_best.clear();
                    S_best.addAll(Sc_Add_V1_Add_VV1);
                }
            } else {
                int b = LowerBoundAlgorithm.lowerBoundAlgorithm(V, V0, V1, VV0, VV1);
                if (b <= u) {
                    backTracking();
                }
            }
        }

        //Step5：返回上一层前执行VV1=VV1\{vi}，VV5=VV5∪{vi}
        VV1.remove(curNode);
        V.add(curNode);
        V_list.add(curNode);
        W.clear();
        W.addAll(W_bak_left);
        Collections.sort(V_list);
        System.out.println("恢复状态并重新按照序号排序完毕后V_list的状态：");
        V_list.forEach(System.out::println);
    }
}
