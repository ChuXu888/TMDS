package com.chuxu.main.part04_lowerBoundAlgorithm;

import com.chuxu.entity.CloneObject;
import com.chuxu.entity.Node;
import com.chuxu.main.part02_properties.Properties;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.chuxu.main.MainAlgorithm.*;

public class LowerBoundAlgorithm {

    //下界子算法无解时的返回值，回头还要再视情况修改
    public static final int b_limit = Integer.MAX_VALUE / 2 + 1;

    public static int lowerBoundAlgorithm(LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1, LinkedHashSet<Node> VV0, LinkedHashSet<Node> VV1) {
//        System.out.println("============================================================================");
//        System.out.println("============================================================================");
//        System.out.println("现在进入下界子算法");

        //下界子算法从一开始就不是全局降阶，都是在自己的一个小空间内自己玩，所以下界子算法中一开始就要克隆所有的形参，
        //不对主函数中传入的全局变量造成影响。V其实是VV5=V\V0\V1\VV0\VV1
        CloneObject cloneObject = cloneObject(V, V0, V1, VV0, VV1);
        V = cloneObject.getV();
        V0 = cloneObject.getV0();
        V1 = cloneObject.getV1();
        VV0 = cloneObject.getVV0();
        VV1 = cloneObject.getVV1();
        //将全局的W也克隆一份来操作，其实区别就是W_global已经去掉了降阶过程中涉及的一些节点
        //后续计算W_local时，遍历W_global而不是遍历VV5(所有节点)可以省一点时间
        LinkedHashSet<Node> W_global = new LinkedHashSet<>();
        for (Node node : W) {
            W_global.add(node.clone());
        }

//        //利用性质2判断一波无解的情况
//        if (!Properties.property02(W, V, V1, VV1)) {
//            return b_limit;
//        }

        //Step1：初始化集合Vtemp=V1∪VV1，初始化b=|V1∪VV1)|；
        LinkedHashSet<Node> V_temp = new LinkedHashSet<>();
        V_temp.addAll(V1);
        V_temp.addAll(VV1);
        int b = V_temp.size();

        while (true) {
            //Step2： 计算集合W={vi|vi∊V且|N(G,vi)∩Vtemp|<r}，若|W|=0，转至Step4；否则∀vi∊W，计算R(vi)=r-|N(G,vi)∩Vtemp)|；
            //对于W_global，一遍遍历一边删除已经满足N(G,vi)∩Vtemp|>=r的节点，对于N(G,vi)∩Vtemp|<r的节点也设置好相关属性
            //W_global反正是克隆的，修改了属性也没关系，影响范围不会超出下界子算法
            Iterator<Node> iterator = W_global.iterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();
                int countsInVtemp = 0;
                for (Node node2 : V_temp) {
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

            //若|W|=0，转至Step4；
            if (W_global.size() == 0) {
                break;
            }
            //Step3：将W中结点按照R(vi)的值从大到小排序，假设vk表示排序后W中的第一个结点；
            Node vk = null;
            int maxValue = Integer.MIN_VALUE;
            for (Node node : W_global) {
                if (node.getRemainCount() > maxValue) {
                    maxValue = node.getRemainCount();
                    vk = node;
                }
            }
            //循环结束后，就找到了W中R(vi)的值最大的结点vk，
            //执行b=b+R(vk)，Vtemp=Vtemp∪N(G[VV5],vk)；转至Step2；
//            System.out.println("vk = " + vk);
            b += vk.getRemainCount();
            for (Integer neighborId : vk.getNeighborIds()) {
                V_temp.addAll(VV5.stream().filter(node -> node.getId().equals(neighborId)).collect(Collectors.toList()));
            }
        }
        //Step4：由性质4，b=max{b,r+1}
        return Math.max(b, r + 1);
    }

    private static CloneObject cloneObject(LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1, LinkedHashSet<Node> VV0, LinkedHashSet<Node> VV1) {
        LinkedHashSet<Node> V_b = new LinkedHashSet<>();
        for (Node node : V) {
            V_b.add(node.clone());
        }
        LinkedHashSet<Node> V0_b = new LinkedHashSet<>();
        for (Node node : V0) {
            V0_b.add(node.clone());
        }
        LinkedHashSet<Node> V1_b = new LinkedHashSet<>();
        for (Node node : V1) {
            V1_b.add(node.clone());
        }
        LinkedHashSet<Node> VV0_b = new LinkedHashSet<>();
        for (Node node : VV0) {
            VV0_b.add(node.clone());
        }
        LinkedHashSet<Node> VV1_b = new LinkedHashSet<>();
        for (Node node : VV1) {
            VV1_b.add(node.clone());
        }
        return new CloneObject(V_b, V0_b, V1_b, VV0_b, VV1_b);
    }
}
