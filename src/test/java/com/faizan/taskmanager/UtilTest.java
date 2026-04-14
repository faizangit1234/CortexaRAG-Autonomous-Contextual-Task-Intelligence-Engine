package com.faizan.taskmanager;

import com.faizan.taskmanager.Util.VectorUtils;

import java.util.List;

public class UtilTest {
    public static void main(String[] args) {

        List<Double> v1 = List.of(1.0, 0.0);
        List<Double> v2 = List.of(1.0, 0.0);
        List<Double> v3 = List.of(0.0, 1.0);

        System.out.println(VectorUtils.cosineSimilarity(v1, v2)); // ~1.0
        System.out.println(VectorUtils.cosineSimilarity(v1, v3)); // ~0.0
    }
}
