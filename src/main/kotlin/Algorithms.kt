package main.kotlin

import kotlin.math.max

enum class Algorithms {

    /*KNAPSACK {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            return knapSack(c, w, p, n)
        }
    },*/

    KNAPSACK_DP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val m = Array(2) { IntArray(c + 1) }

            for (i in 1..n) {
                for (j in 1..c) {
                    if (j >= w[i-1])
                        m[i and 1][j] = max(m[(i - 1) and 1][j], m[(i - 1) and 1][j - w[i - 1]] + p[i - 1])
                    else
                        m[i and 1][j] = m[(i - 1) and 1][j]
                }
            }

            return m[n and 1][c]
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
    };


    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
}