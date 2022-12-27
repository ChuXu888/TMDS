package com.chuxu.main.part06_backTrackingAlgorithm;

import com.chuxu.entity.Node;
import com.chuxu.main.part02_properties.Properties;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

import static com.chuxu.main.MainAlgorithm_OnlyBackTracking.*;

public class BackTrackingAlgorithmWithoutMathematicalProperties {

    public static void backTrackingAlgorithm() {
        System.out.println("============================================================================");
        System.out.println("============================================================================");
        System.out.println("现在进入回溯子算法");
        System.out.println("======================================================");
        System.out.println("W：");
        W.forEach(System.out::println);
        System.out.println("V(VV5)：");
        V.forEach(System.out::println);
        System.out.println("V_list(VV5)：");
        V_list.forEach(System.out::println);

        backTracking();
    }

    public static void backTracking() {
        //二叉树搜索次数
        count++;

        //Step1：当|VV5|=0时，此时所有的结点都确定下来了开或者不开，才形成了一个完整的方案，才是叶子节点
        if (V_list.size() == 0) {
            //叶子节点搜索次数
            countOfLeaf++;
            //若|W|=0，则得到可行解Sk=(V1∪VV1)==>通过性质2判断是否W中的【每个结点】均有至少r个节点位于V1∪VV1中
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

        //判断此时是否满足性质2【判断无解的还是留着吧】的条件，若满足则该情况下无可行解，右子树剪枝，转至Step3
        //若返回false，则无可行解，右子树剪枝，若返回true则继续
        if (Properties.property02(W, V, V1, VV1)) {
            backTracking();
        }

        //Step3：返回上一层前执行VV0=VV0\{vi}，VV1=VV1\Vtemp，VV5=VV5∪{vi}∪Vtemp，还有W的状态
        //把这部分放前面，然后push(curNode)，从而保证curNode在栈顶
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

        //此时若|V1∪VV1|≥u，则基于当前情况后续求得的解不会优于当前上界，左子树剪枝，转至Step5；
        LinkedHashSet<Node> V1_Add_VV1 = new LinkedHashSet<>(V1);
        V1_Add_VV1.addAll(VV1);
        //W的备份，左子树假设某个节点在最小支配阈值集中，要更新W
        LinkedHashSet<Node> W_bak_left = new LinkedHashSet<>(W);
        //由于左子树是假设在最小支配阈值集中，所以要更新W
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

        backTracking();

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
