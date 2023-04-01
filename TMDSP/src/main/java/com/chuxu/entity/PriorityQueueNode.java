package com.chuxu.entity;

import java.util.LinkedHashSet;
import java.util.List;

public class PriorityQueueNode {
    private LinkedHashSet<Node> VV1;
    private LinkedHashSet<Node> VV0;
    private List<Node> remainV;
    private Double lowerBound;

    public PriorityQueueNode() {
    }

    public PriorityQueueNode(LinkedHashSet<Node> VV1, LinkedHashSet<Node> VV0, List<Node> remainV, Double lowerBound) {
        this.VV1 = VV1;
        this.VV0 = VV0;
        this.remainV = remainV;
        this.lowerBound = lowerBound;
    }

    public LinkedHashSet<Node> getVV1() {
        return VV1;
    }

    public void setVV1(LinkedHashSet<Node> VV1) {
        this.VV1 = VV1;
    }

    public LinkedHashSet<Node> getVV0() {
        return VV0;
    }

    public void setVV0(LinkedHashSet<Node> VV0) {
        this.VV0 = VV0;
    }

    public List<Node> getRemainV() {
        return remainV;
    }

    public void setRemainV(List<Node> remainV) {
        this.remainV = remainV;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public String toString() {
        return "PriorityQueueNode{" +
                "VV1=" + VV1 +
                ", VV0=" + VV0 +
                ", remainV=" + remainV +
                ", lowerBound=" + lowerBound +
                '}';
    }
}
