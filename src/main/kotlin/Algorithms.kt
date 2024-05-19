package main.kotlin

enum class Algorithms {

    /*KNAPSACK {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapSack(c, w, p, n)
        }
    },*/

    KNAPSACK2 {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapSack2(c, w, p, n)
        }
    };

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
}