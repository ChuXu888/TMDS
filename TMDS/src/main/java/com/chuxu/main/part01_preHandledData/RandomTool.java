package com.chuxu.main.part01_preHandledData;

import com.chuxu.entity.Node;

import java.util.*;

public class RandomTool {

    public static int n = 24;  //问题规模
    public static final int r = 2;  //阈值
    public static int INF = 10000;  //代表不连通
    public static int[][] neighborMatrix = new int[n][n];  //距离矩阵
    public static final Random random = new Random();  //随机数工具
    public static final List<Node> nodes = new ArrayList<>();  //节点列表

    public static void main(String[] args) {
        initialNeighborMatrix();
        for (int[] matrix : neighborMatrix) {
            System.out.print("{");
            for (int j = 0; j < neighborMatrix[0].length; j++) {
                if (j == neighborMatrix[0].length - 1) {
                    System.out.printf("%7d", matrix[j]);
                } else {
                    System.out.printf("%7d,", matrix[j]);
                }
            }
            System.out.println("},");
        }
    }

    //1.初始化邻接矩阵
    public static void initialNeighborMatrix() {
        //①先将所有元素都设为INF
        for (int i = 0; i < neighborMatrix.length; i++) {
            for (int j = 0; j < neighborMatrix[0].length; j++) {
                neighborMatrix[i][j] = INF;
            }
        }
        //②然后将对角线元素设为0
        for (int i = 0; i < neighborMatrix.length; i++) {
            neighborMatrix[i][i] = 0;
        }
        //③然后开始设置邻接节点
        //使得每个点至少与r个节点邻接，至多与r+3个节点邻接【最好还要想办法让刚好有r个邻接点的节点多一些】
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indexList.add(i);
        }

        //定义一个向量，用于存储每个节点的邻接结点个数
        int[] neighborCounts = new int[n];
        for (int i = 0; i < neighborCounts.length; i++) {
            //将每个节点的邻接节点个数设置为[r,r+4]
            neighborCounts[i] = random.nextInt(4) + r;
        }

        //对于每个节点对应的neighborCount，从indexList_copy中随机取一个索引，将邻接矩阵中该值设置为1，
        //同时设置邻接矩阵中其对称位置也为1，不过要记得其对称的行代表的节点的neighborCount也需要-1
        for (int i = 0; i < neighborMatrix.length; i++) {
            List<Integer> indexList_copy = new ArrayList<>(indexList);
            int curNeighborCount = neighborCounts[i];
            while (curNeighborCount > 0) {
                int curRandomIndex = -1;
                //specificNodeIndex只有在第一轮是等于curRandomIndex的，后续indexList_copy.remove(curRandomIndex);
                //之后，它们就完全区分开了
                int specificNodeIndex = -1;
                //此处的specificNodeIndex不能等于i，因为对角线元素只能为0
                do {
                    curRandomIndex = random.nextInt(indexList_copy.size());
                    specificNodeIndex = indexList_copy.get(curRandomIndex);
                } while (specificNodeIndex == i);
                //设置两个对称位置为1
                neighborMatrix[i][specificNodeIndex] = 1;
                neighborMatrix[specificNodeIndex][i] = 1;
                //将对称位置的neighborCount-1
                neighborCounts[specificNodeIndex]--;
                curNeighborCount--;
                //从indexList_copy中根据索引移除该已经被设置过的节点
                indexList_copy.remove(curRandomIndex);
            }
        }

        //④再将矩阵对称化
//        for (int i = 0; i < neighborMatrix.length; i++) {
//            for (int j = i; j < neighborMatrix[0].length; j++) {
//                neighborMatrix[j][i] = neighborMatrix[i][j];
//            }
//        }

    }
}