package com.chuxu.entity;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Objects;

@Data
public class Node implements Cloneable,Comparable<Node>{
    private Integer id;
    private Integer degree;
    private LinkedHashSet<Integer> neighborIds;
    //countsInDominatingSet属性和remainCount属性的关系为：
    //若countsInDominatingSet<=r，则remainCount=r-countsInDominatingSet；
    //若countsInDominatingSet>r，则remainCount=0；
    private Integer countsInDominatingSet;
    private Integer remainCount;

    //无参构造方法
    public Node() {
    }

    //仅id一个参数的构造方法，其他属性慢慢计算
    public Node(Integer id) {
        this.id = id;
    }

    public Node(Integer id, Integer degree, LinkedHashSet<Integer> neighborIds) {
        this.id = id;
        this.degree = degree;
        this.neighborIds = neighborIds;
    }

    @Override
    public Node clone() {
        try {
            // TODO: 复制此处的可变状态，这样此克隆就不能更改初始克隆的内部
            return (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    //重写hashCode()方法和equal()方法，只要它们的id相同，就认为它们是同一个结点，方便Set进行增删改操作
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id.equals(node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Node o) {
        return this.id.compareTo(o.getId());
    }
}
