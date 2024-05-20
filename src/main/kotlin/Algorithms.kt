package main.kotlin

enum class Algorithms {

    // KNAPSACK {
    //     override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
    //         return knapSack3(c, w, p, n, timeout)
    //     }
    // },

    HEUR {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
            return heur(c, w, p, n)
        }
    },

    /*,

    KNAPSACK_LOCAL_SEARCH_SWAP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n)
            val sol = localSearchKnapsack.localSearch(false, timeout)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_LOCAL_SEARCH_FLIP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, 1)
            val sol = localSearchKnapsack.localSearch(false, timeout)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },
    KNAPSACK_LOCAL_SEARCH_SWAP_GREEDY {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n)
            val sol = localSearchKnapsack.localSearch(true, timeout)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_LOCAL_SEARCH_FLIP_GREEDY {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, 1)
            val sol = localSearchKnapsack.localSearch(true, timeout)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    }*/;

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long): Int
}