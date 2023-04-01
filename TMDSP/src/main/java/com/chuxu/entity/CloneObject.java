package com.chuxu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloneObject implements Cloneable{

    LinkedHashSet<Node> V = new LinkedHashSet<>();
    LinkedHashSet<Node> V0 = new LinkedHashSet<>();
    LinkedHashSet<Node> V1 = new LinkedHashSet<>();
    LinkedHashSet<Node> VV0 = new LinkedHashSet<>();
    LinkedHashSet<Node> VV1 = new LinkedHashSet<>();

    @Override
    public CloneObject clone() {
        try {
            // TODO: 复制此处的可变状态，这样此克隆就不能更改初始克隆的内部
            return (CloneObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public CloneObject(LinkedHashSet<Node> v, LinkedHashSet<Node> v0, LinkedHashSet<Node> v1) {
        V = v;
        V0 = v0;
        V1 = v1;
    }
}
