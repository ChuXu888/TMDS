package com.chuxu.main.part02_properties;

import com.chuxu.entity.Node;
import com.chuxu.main.MainAlgorithm;
import com.chuxu.main.part04_lowerBoundAlgorithm.LowerBoundAlgorithm;

import java.util.LinkedHashSet;

import static com.chuxu.main.MainAlgorithm.V;
import static com.chuxu.main.MainAlgorithm.r;

public class Properties {

    //性质1：当|VV0∪VV1|=0时，若图G=(V,E)中存在|N(G,v)\V0|< r的结点v，则该问题无可行解。
    public static boolean property01(LinkedHashSet<Node> W, LinkedHashSet<Node> V, LinkedHashSet<Node> V1) {
        //V\V0=V1∪V5(V)
        LinkedHashSet<Node> V_Add_V1 = new LinkedHashSet<>(V);
        V_Add_V1.addAll(V1);

        for (Node node1 : W) {
            int countsInPotentialSet = 0;
            for (Node node2 : V_Add_V1) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    countsInPotentialSet++;
                }
            }
            if (countsInPotentialSet < r) {
                System.out.println(node1);
                return false;
            }
        }
        return true;  //如果程序能执行到这里，就返回true
    }

//    //性质2：当|VV0∪VV1|≠0时，若图G=(V,E)中存在|N(G,v)\V0\VV0|<r的结点v，则该情况下无可行解。
//    public static boolean property02(LinkedHashSet<Node> W, LinkedHashSet<Node> V, LinkedHashSet<Node> V1, LinkedHashSet<Node> VV1) {
//        //V\V0\VV0=VV5(V)∪V1∪VV1
//        LinkedHashSet<Node> V_Add_V1_Add_VV1 = new LinkedHashSet<>(V);
//        V_Add_V1_Add_VV1.addAll(V1);
//        V_Add_V1_Add_VV1.addAll(VV1);
//
//        for (Node node1 : W) {
//            int countsInPotentialSet = 0;
//            for (Node node2 : V_Add_V1_Add_VV1) {
//                if (node2.getNeighborIds().contains(node1.getId())) {
//                    countsInPotentialSet++;
//                }
//            }
//            if (countsInPotentialSet < r) {
////                countOfProperty02++;
//                return false;
//            }
//        }
//        //如果程序能执行到这里，说明每个W中的节点都至少有r个邻接结点在V1∪VV1∪VV5中，就返回true。
//        //当为叶子节点时，VV5(V)={}
//        return true;
//    }

    //性质2：当|VV0∪VV1|=0时，若图G=(V,E)中存在|N(G,v)\V0|=r的结点v，则(N(G,v)\V0)中的所有结点一定在最优解中，此时将(N(G,v)\V0)加入V1。
    public static LinkedHashSet<Integer> property02(LinkedHashSet<Node> W, LinkedHashSet<Node> V, LinkedHashSet<Node> V1) {
        //V\V0=V1∪V5=V1∪V
        LinkedHashSet<Node> V_Add_V1 = new LinkedHashSet<>(V);
        V_Add_V1.addAll(V1);
        LinkedHashSet<Integer> nodeIdsOfProperty03 = new LinkedHashSet<>();

        for (Node node1 : W) {
            int countsInPotentialSet = 0;
            for (Node node2 : V_Add_V1) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    countsInPotentialSet++;
                }
            }
            if (countsInPotentialSet == r) {
                nodeIdsOfProperty03.addAll(node1.getNeighborIds());
                //这里只修改了度刚好为r的结点的相关属性，但其实在这个性质结束后，还应该从整体角度全部【审视一遍所有节点】
                //对于本例，最终只剩下3个结点的需求没有被满足
//                node1.setCountsInDominatingSet(r);  //进行全局属性修改
//                node1.setRemainCount(0);  //进行全局属性修改
            }
        }
        return nodeIdsOfProperty03;
    }

    //性质3：当|VV0∪VV1|≠0时，若图G=(V,E)中存在|N(G,v)\V0\VV0|=r的结点v，则(N(G,v)\V0\VV0)中的所有结点一定在最优解中，此时将(N(G,v)\V0\VV0)加入VV1。
    public static LinkedHashSet<Integer> property03(LinkedHashSet<Node> W, LinkedHashSet<Node> V, LinkedHashSet<Node> V1, LinkedHashSet<Node> VV1) {
        //V\V0\VV0=V1∪VV1∪VV5=V1∪VV1∪V
        LinkedHashSet<Node> V_Add_V1_Add_VV1 = new LinkedHashSet<>(V);
        V_Add_V1_Add_VV1.addAll(V1);
        V_Add_V1_Add_VV1.addAll(VV1);

        LinkedHashSet<Integer> nodeIdsOfProperty04 = new LinkedHashSet<>();

        for (Node node1 : W) {
            int countsInPotentialSet = 0;
            for (Node node2 : V_Add_V1_Add_VV1) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    countsInPotentialSet++;
                }
            }
            if (countsInPotentialSet == r) {
                nodeIdsOfProperty04.addAll(node1.getNeighborIds());
//                node1.setCountsInDominatingSet(r);  //进行全局属性修改
//                node1.setRemainCount(0);  //进行全局属性修改
            }
        }
        return nodeIdsOfProperty04;
    }

    //性质5：若图G=(V,E)中所有结点的度deg(G,v)均为r，则此时最优解S*=V，且此时多项式时间内可求得最优解。
    public static boolean property05(LinkedHashSet<Node> V) {
        for (Node node : V) {
            if (node.getDegree() != r) {
                return false;
            }
        }
        return true;
    }

    //性质6：若图G=(V,E)为完全图，当(r+1)≤n时，V中任意(r+1)个结点组成的集合均为最优解。
    public static boolean property06(LinkedHashSet<Node> V) {
        for (Node node : V) {
            if (node.getDegree() != V.size() - 1) {
                return false;
            }
        }
        return true;
    }

    //性质8：对于给定的无向图G=(V,E)和阈值r，当|VV0∪VV1|=0时，若假设某个结点vi∊V5在最优解中，此时VV0={}，VV1={vi}，如果此时的下界b大于上界u，则vi∊V0，
    //也即vi一定不在最优解中，应将vi加入V0；判断结束后恢复VV1={}。
    public static LinkedHashSet<Node> property08(LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1) {
        //定义不开设【加入V0】的结点集合，返回到主函数中再进行处理
        LinkedHashSet<Node> deleteNodes = new LinkedHashSet<>();
        //从主算法中获取上界
        double u = MainAlgorithm.u;
        //V\V0\V1=V5(V)
        LinkedHashSet<Node> newV = new LinkedHashSet<>(V);
        LinkedHashSet<Node> newV0 = new LinkedHashSet<>(V0);
        LinkedHashSet<Node> newV1 = new LinkedHashSet<>(V1);

        for (Node curNode : newV) {
            //定义VV1
            LinkedHashSet<Node> VV1 = new LinkedHashSet<>();
            VV1.add(curNode);
            //新建一个列表用来存储VV5\{curNode}的情况，以免形成一边遍历一边修改的情况，并且也保持了newV始终如一
            LinkedHashSet<Node> newV_Minus_curNode = new LinkedHashSet<>(newV);
            newV_Minus_curNode.remove(curNode);
            //调用下界子算法计算下界，以F1_temp作为形参，但是下界子算法同时需要F0和FF0的，所以这些都要根据当前元素更新之后再传入下界子算法
            double b = LowerBoundAlgorithm.lowerBoundAlgorithm(newV_Minus_curNode, newV0, newV1, new LinkedHashSet<>(), VV1);
//            System.out.println("当前假设node：" + curNode.getId() + "开设时，下界为：" + b);
            //如果得到的下界大于上界，那么fj一定不开设，将其加入F0，可以直接在主函数中的全局变量上操作
            if (b > u) {
                deleteNodes.add(curNode);
            }
        }
        //在调用方进行非空判断
        return deleteNodes;
    }

    //性质9：对于给定的无向图G=(V,E)和阈值r，当|VV0∪VV1|=0时，若假设某个结点vi∊V5不在最优解中，此时VV0={vi}，VV1={}，如果此时的下界b大于上界u，则vi∊V1
    //也即vi一定在最优解中，应将vi加入V1；判断结束后恢复VV0={}。
    public static LinkedHashSet<Node> property09(LinkedHashSet<Node> V, LinkedHashSet<Node> V0, LinkedHashSet<Node> V1) {
        //定义开设【加入V1】的结点集合，返回到主函数中再进行处理
        LinkedHashSet<Node> addNodes = new LinkedHashSet<>();
        //从主算法中获取上界
        double u = MainAlgorithm.u;
        //V\V0\V1=V5(V)
        LinkedHashSet<Node> newV = new LinkedHashSet<>(V);
        LinkedHashSet<Node> newV0 = new LinkedHashSet<>(V0);
        LinkedHashSet<Node> newV1 = new LinkedHashSet<>(V1);

        for (Node curNode : newV) {
            //定义VV0
            LinkedHashSet<Node> VV0 = new LinkedHashSet<>();
            VV0.add(curNode);
            //新建一个列表用来存储VV5\{curNode}的情况，以免形成一边遍历一边修改的情况，并且也保持了newV始终如一
            LinkedHashSet<Node> newV_Minus_curNode = new LinkedHashSet<>(newV);
            newV_Minus_curNode.remove(curNode);
            //调用下界子算法计算下界，以F1_temp作为形参，但是下界子算法同时需要F0和FF0的，所以这些都要根据当前元素更新之后再传入下界子算法
            double b = LowerBoundAlgorithm.lowerBoundAlgorithm(newV_Minus_curNode, newV0, newV1, VV0, new LinkedHashSet<>());
//            System.out.println("当前假设node：" + curNode.getId() + "不开设时，下界为：" + b);
            //如果得到的下界大于上界，那么fj一定不开设，将其加入F0，可以直接在主函数中的全局变量上操作
            if (b > u) {
                addNodes.add(curNode);
            }
        }
        //在调用方进行非空判断
        return addNodes;
    }

    //性质10：对于给定的无向图G=(V,E)和阈值r，在最小支配阈值集问题的求解过程中，当|W|≠0时，令集合Sc=  ，若∀vi∊W有R(vi)=1且|Sc|=1，
    //此时如果|VV0∪VV1|=0，则Sc一定在最优解中，此时将Sc加入V1；V5=V\V0\V1，应该已经即时更新好了，直接传进来就好了
    public static LinkedHashSet<Node> property10_1(LinkedHashSet<Node> W, LinkedHashSet<Node> V5) {
        //应该先判断∀vi∊W有R(vi)=1是否成立，若不成立，则后面的都不用弄了
        for (Node node : W) {
            if (node.getRemainCount() != 1) {
                //返回一个空集，调用方进行判断，如果返回值为空集，那就说明不满足性质10的条件
                return new LinkedHashSet<>();
            }
        }
        //程序能走到这里，说明∀vi∊W有R(vi)=1是成立的，才有必要继续计算Sc
        LinkedHashSet<Node> Sc = new LinkedHashSet<>(V);  //由于要取交集，故初始状态这个应该弄一个全的
        for (Node node1 : W) {
            LinkedHashSet<Node> neighborsInV5OfNode1 = new LinkedHashSet<>();
            for (Node node2 : V5) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    neighborsInV5OfNode1.add(node2);
                }
            }
            Sc.retainAll(neighborsInV5OfNode1);  //集合取交集
        }
        //如果|Sc|=1，那么就满足性质10的使用条件了
        if (Sc.size() == 1) {
            return Sc;
        }
        return new LinkedHashSet<>();
    }

    //性质10-2：如果|VV0∪VV1|≠0，则将Sc加入VV1。VV5=V5\VV0\VV1，应该也已经即时更新好了，直接传进来就好了
    public static LinkedHashSet<Node> property10_2(LinkedHashSet<Node> W, LinkedHashSet<Node> VV5) {
        //应该先判断∀vi∊W有R(vi)=1是否成立，若不成立，则后面的都不用弄了
        for (Node node : W) {
            if (node.getRemainCount() != 1) {
                //返回一个空集，调用方进行判断，如果返回值为空集，那就说明不满足性质10的条件
                return new LinkedHashSet<>();
            }
        }
        //程序能走到这里，说明∀vi∊W有R(vi)=1是成立的，才有必要继续计算Sc
        LinkedHashSet<Node> Sc = new LinkedHashSet<>(V);  //由于要取交集，故初始状态这个应该弄一个全的
        for (Node node1 : W) {
            LinkedHashSet<Node> neighborsInVV5OfNode1 = new LinkedHashSet<>();
            for (Node node2 : VV5) {
                if (node2.getNeighborIds().contains(node1.getId())) {
                    neighborsInVV5OfNode1.add(node2);
                }
            }
            Sc.retainAll(neighborsInVV5OfNode1);  //集合取交集
        }
        //如果|Sc|=1，那么就满足性质10的使用条件了
        if (Sc.size() == 1) {
            return Sc;
        }
        return new LinkedHashSet<>();
    }
}
