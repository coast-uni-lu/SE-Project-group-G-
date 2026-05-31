package com.group_g.demo.model;

// score sections used for quiz questions and results
public enum QuizzCategory {
    CALCULUS("Mathematics - Calculus"),
    DISCRETE_MATHEMATICS("Discrete Mathematics"),
    PROGRAMMING_BASICS("Programming Basics"),
    INTRODUCTION_A_L_INFORMATIQUE("Basic Computer and Algorithm Concepts");

    private final String displayName;

    QuizzCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
