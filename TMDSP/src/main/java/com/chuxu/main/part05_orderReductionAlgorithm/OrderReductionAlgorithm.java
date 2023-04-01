package com.chuxu.main.part05_orderReductionAlgorithm;

import com.chuxu.entity.Node;
import com.chuxu.main.part02_properties.Properties;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.chuxu.main.MainAlgorithm.*;

public class OrderReductionAlgorithm {

    public static void orderReductionAlgorithm() {
//        System.out.println("============================================================================");
//        System.out.println("============================================================================");
//        System.out.println("现在进入降阶子算法");
//
//        System.out.println("======================================================");
//        System.out.println("W：");
//        W.forEach(System.out::println);
//        System.out.println("V(V5)：");
//        V.forEach(System.out::println);

        boolean property08Flag = false;
        boolean property09Flag = false;

        //Step1：判断上界子算法处理后图G是否满足性质10的条件，若满足则得到最优解S*=V1，γ(G)=|V1|，整个算法结束；
        //如果性质10返回了一个空列表，说明不满足性质10的使用条件
        LinkedHashSet<Node> Sc = Properties.property10_2(W, V);
        if (Sc.size() != 0) {
            V1.addAll(Sc);
            γ_G = V1.size();
//            System.out.println("满足性质10的条件，Sc∪V1即为最优解！");
//            V1.forEach(System.out::println);
//            System.out.println("γ_G = " + γ_G);
            System.exit(0);
        }

        //Step2：利用性质8对问题进行降阶；
        LinkedHashSet<Node> deleteNodes = Properties.property08(V, V0, V1);
        if (deleteNodes.size() != 0) {
            property08Flag = true;
            System.out.println("========================================================================");
//            System.out.println("由性质8确定的一定不开设的备选点为：");
//            deleteNodes.forEach(System.out::println);
            V.removeAll(deleteNodes);
            V0.addAll(deleteNodes);

            //若Step2处理后V0发生变化，转至Step4；
            //Step4：判断此时是否满足性质1的条件，若满足，则该问题无可行解，整个算法结束；
            if (!Properties.property01(W, V, V0)) {
                System.out.println("该问题无解！");
                System.exit(0);
            }

            //Step5：利用性质3对问题进行降阶；
            LinkedHashSet<Integer> nodeIdsOfProperty03 = Properties.property02(W, V, V0);
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
            Iterator<Node> iterator = W.iterator();
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
//            System.out.println("经过性质3处理后的W:");
//            W.forEach(System.out::println);
        }

        //Step6：利用性质9对问题进行降阶；
        LinkedHashSet<Node> addNodes = Properties.property09(V, V0, V1);
        if (addNodes.size() != 0) {
            property09Flag = true;
//            System.out.println("========================================================================");
//            System.out.println("由性质9确定的一定开设的备选点为：");
//            addNodes.forEach(System.out::println);
            V.removeAll(deleteNodes);
            V1.addAll(deleteNodes);
        }

        //Step7：若相较降阶子算法开始时(V0∪V1)发生变化，则转至Step8，否则降阶子算法结束；
        if (property08Flag || property09Flag) {
            //Step8：再次判断图G是否满足性质10的条件，若满足则得到最优解S*=V1，γ(G)=|V1|，整个算法结束。
            LinkedHashSet<Node> Sc_2 = Properties.property10_2(W, V);
            if (Sc_2.size() != 0) {
                V1.addAll(Sc_2);
                γ_G = V1.size();
//                System.out.println("满足性质10的条件，Sc∪V1即为最优解！");
//                V1.forEach(System.out::println);
//                System.out.println("γ_G = " + γ_G);
                System.exit(0);
            }
        }
    }
}