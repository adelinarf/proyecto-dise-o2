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
    },

    KNAPSACK_HEURISTICS {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapsack_heuristics(c, w, p, n,"QBH01")
        }
    };

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
}