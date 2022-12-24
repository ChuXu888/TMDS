package com.chuxu.main;

import com.chuxu.entity.Node;
import com.chuxu.main.part01_preHandledData.PreHandleData01;
import com.chuxu.main.part02_properties.Properties;
import com.chuxu.main.part03_upperBoundAlgorithm.UpperBoundAlgorithm;
import com.chuxu.main.part05_orderReductionAlgorithm.OrderReductionAlgorithm;
import com.chuxu.main.part06_backTrackingAlgorithm.BackTrackingAlgorithm;
import com.chuxu.main.part06_backTrackingAlgorithm.BackTrackingAlgorithmWithoutMathematicalProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class MainAlgorithm_OnlyBackTracking {
    public static final int[][] neighborMatrix = PreHandleData01.neighborMatrix;  //邻接矩阵
    public static final int r = PreHandleData01.r;  //阈值
    public static LinkedHashSet<Node> V = new LinkedHashSet<>();  //结点列表，作为变量存储，不对它进行操作
    public static List<Node> V_list = new ArrayList<>();  //结点列表，作为变量存储，不对它进行操作
    public static LinkedHashSet<Node> V1 = new LinkedHashSet<>();  //一定加入最小支配阈值集的集合
    public static LinkedHashSet<Node> V0 = new LinkedHashSet<>();  //一定不加入最小支配阈值集的集合
    public static LinkedHashSet<Node> V5 = new LinkedHashSet<>();  //V5=V\V1\V0
    public static LinkedHashSet<Node> VV1 = new LinkedHashSet<>();  //假设加入最小支配阈值集的集合
    public static LinkedHashSet<Node> VV0 = new LinkedHashSet<>();  //假设不加入最小支配阈值集的集合
    public static LinkedHashSet<Node> VV5 = new LinkedHashSet<>();  //VV5=V\V1\V0\VV1\VV0，现在将VV5当作初始不动的V使用，而将原本应该不动的V当作V和VV5在使用
    public static LinkedHashSet<Node> W = new LinkedHashSet<>();  //尚未满足需求的结点集合，初始化W=V
    public static double u = Double.MAX_VALUE;  //全局上界
    public static LinkedHashSet<Node> S_best = new LinkedHashSet<>();  //当前状态下已知最优目标值对应的开设设施集合
    public static double γ_G = Double.MAX_VALUE;  //该问题的最小支配阈值集的支配数
    public static int count = 0;  //二叉树搜索次数(包含根结点)
    public static int countOfLeaf = 0;  //二叉树叶子结点搜索次数

    public static void main(String[] args) {
        //记录程序开始时间
        long start = System.currentTimeMillis();

        //Step1：数据预处理和初始化
        PreHandleData01.preHandle();
        deepCopy();

        //Step2：判断给定的无向图G=(V,E)是否满足性质1的条件，若满足，则该问题无可行解，主算法结束；
        if (!Properties.property01(W, V, V1)) {
            System.out.println("图G=(V,E)中存在|N(G,v)\\V0|< r的结点v，该问题无可行解！");
            System.exit(0);
        }

        //Step3：判断给定的无向图G=(V,E)是否满足性质6的条件，若满足，则S*=V，γ(G)=|V|，主算法结束；
        if (Properties.property06(V)) {
            System.out.println("图G=(V,E)中所有结点的度deg(G,v)均为r，则此时最优解S*=V：");
            S_best = new LinkedHashSet<>(V);
            γ_G = V.size();
            System.exit(0);
        }

        //Step4：判断给定的无向图G=(V,E)是否满足性质7的条件，若满足，则V中任意(r+1)个结点组
        //成的集合均为图G的最小支配阈值集S*，γ(G)=(r+1)，主算法结束；
        if (Properties.property07(V)) {
            System.out.println("图G=(V,E)为完全图，当(r+1)≤n时，V中任意(r+1)个结点组成的集合均为最优解：");
            System.exit(0);
        }

        printAll();

        //Step7：回溯子算法
        initialList();
        BackTrackingAlgorithmWithoutMathematicalProperties.backTrackingAlgorithm();

        //记录程序结束时间
        long end = System.currentTimeMillis();

        //回溯过程中不会改变F0和F1，所以也可以在最后看降阶效果
        System.out.println("=======================================================");
        System.out.println("进入回溯子算法中进行判断的VV5.size() = " + (VV5.size() - V0.size() - V1.size()));
        System.out.println("二叉树搜索次数为 = " + count);
        System.out.println("二叉树叶子结点搜索次数为 = " + countOfLeaf);
        //回溯子算法结束后，打印相关变量
        γ_G = u;
        System.out.println("=================================================");
        System.out.println("S_best：");
        S_best.forEach(System.out::println);
        System.out.println("γ_G = " + γ_G);
        System.out.println("程序运行总时间为：" + (end - start) / 1000.0 + "s");
    }

    public static void initialList() {
        V_list.addAll(V);
        Collections.sort(V_list);
        V_list.forEach(System.out::println);
    }

    public static void printAll() {
        System.out.println("============================================================================");
        System.out.println("打印当前全局变量：");
        System.out.println("V：");
        V.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("V1：");
        V1.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("V0：");
        V0.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("VV1：");
        VV1.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("VV0：");
        VV0.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("VV5：");
        VV5.forEach(System.out::println);

    }

    public static void deepCopy() {
        LinkedHashSet<Node> nodes = PreHandleData01.nodes;
        for (Node node : nodes) {
            V.add(node.clone());
            W.add(node.clone());
            VV5.add(node.clone());
        }
    }
}