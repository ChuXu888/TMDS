package com.chuxu.main.part01_preHandledData.r_equals_3;

import com.chuxu.entity.Node;

import java.util.LinkedHashSet;

public class PreHandleData_n15_2 {

    public static int INF = 10000;  //代表不连通
    public static final int r = 3;  //阈值
    public static int n = 15;  //问题规模
    public static int[][] neighborMatrix = {
            {0, 1, 1, 1, 1, INF, INF, 1, INF, INF, INF, INF, 1, INF, INF},
            {1, 0, 1, INF, INF, INF, INF, INF, INF, INF, INF, INF, 1, INF, 1},
            {1, 1, 0, INF, INF, 1, INF, INF, 1, INF, 1, 1, INF, INF, INF},
            {1, INF, INF, 0, INF, INF, 1, 1, INF, 1, INF, INF, INF, 1, INF},
            {1, INF, INF, INF, 0, 1, INF, INF, 1, INF, 1, 1, INF, 1, 1},
            {INF, INF, 1, INF, 1, 0, INF, 1, INF, 1, 1, INF, INF, INF, INF},
            {INF, INF, INF, 1, INF, INF, 0, 1, 1, 1, INF, 1, INF, INF, 1},
            {1, INF, INF, 1, INF, 1, 1, 0, INF, INF, INF, INF, INF, INF, INF},
            {INF, INF, 1, INF, 1, INF, 1, INF, 0, INF, 1, INF, INF, 1, 1},
            {INF, INF, INF, 1, INF, 1, 1, INF, INF, 0, INF, INF, INF, INF, INF},
            {INF, INF, 1, INF, 1, 1, INF, INF, 1, INF, 0, INF, 1, INF, INF},
            {INF, INF, 1, INF, 1, INF, 1, INF, INF, INF, INF, 0, 1, INF, INF},
            {1, 1, INF, INF, INF, INF, INF, INF, INF, INF, 1, 1, 0, INF, 1},
            {INF, INF, INF, 1, 1, INF, INF, INF, 1, INF, INF, INF, INF, 0, INF},
            {INF, 1, INF, INF, 1, INF, 1, INF, 1, INF, INF, INF, 1, INF, 0},
    };

    public static LinkedHashSet<Node> nodes = new LinkedHashSet<>();

    public static void preHandle() {
        int nodeCounts = neighborMatrix.length;

        for (int i = 0; i < nodeCounts; i++) {
            Node curNode = new Node(i + 1);
            int curDegree = 0;
            LinkedHashSet<Integer> curNeighborSetIds = new LinkedHashSet<>();
            int[] curNeighborVector = PreHandleData_n15_2.neighborMatrix[i];
            for (int j = 0; j < curNeighborVector.length; j++) {
                if (curNeighborVector[j] == 1) {
                    curDegree++;
                    curNeighborSetIds.add(j + 1);
                }
            }
            curNode.setDegree(curDegree);
            curNode.setNeighborIds(curNeighborSetIds);
            curNode.setCountsInDominatingSet(0);
            curNode.setRemainCount(r);
            nodes.add(curNode);
        }
    }
}