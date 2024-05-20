package main.kotlin

enum class Algorithms {

    /*KNAPSACK {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapSack(c, w, p, n)
        }
    },*/

    KNAPSACK2 {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, callback: (Pair<Int, List<Int>>) -> Unit): Int {
            return knapSack2(c, w, p, n)
        }
    }/*,

    KNAPSACK_HEURISTICS_QBH01 {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapsack_heuristics(c, w, p, n,"QBH01")
        }
    },
    KNAPSACK_HEURISTICS_QBH02 {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapsack_heuristics(c, w, p, n,"QBH02")
        }
    },
    KNAPSACK_HEURISTICS_QBHH {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapsack_heuristics(c, w, p, n,"QBHH")
        }
    },
    KNAPSACK_HEURISTICS_MAX {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapsack_heuristics(c, w, p, n,"max")
        }
    }*/;

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int, callback: (Pair<Int, List<Int>>) -> Unit): Int
}