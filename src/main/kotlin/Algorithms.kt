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
    },

    KNAPSACK_LOCAL_SEARCH_SWAP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n)
            val sol = localSearchKnapsack.localSearch()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_LOCAL_SEARCH_FLIP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, 1)
            val sol = localSearchKnapsack.localSearch()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },
    KNAPSACK_LOCAL_SEARCH_SWAP_GREEDY {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n)
            val sol = localSearchKnapsack.localSearch(true)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_LOCAL_SEARCH_FLIP_GREEDY {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, 1)
            val sol = localSearchKnapsack.localSearch(true)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    };


    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
}