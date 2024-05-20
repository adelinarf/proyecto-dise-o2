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
            val m = Array(n + 1) { IntArray(c + 1) }

            for (i in 1..n) {
                for (j in 1..c) {
                    if (j >= w[i-1])
                        m[i][j] = max(m[i - 1][j], m[i - 1][j - w[i - 1]] + p[i - 1])
                    else
                        m[i][j] = m[i - 1][j]
                }
            }

            val S = IntArray(n) { 0 }
            var i = n
            var j = c
            while (i > 0) {
                if (j-w[i-1] >= 0 && m[i][j] == p[i-1] + m[i - 1][j - w[i-1]]) {
                    S[i-1] = 1
                    j -= w[i-1]
                }
                i--
            }
            
            // println("${m[n][c]} == ${S.zip(p).sumOf { (a, b) -> a * b }}")
            // assert(m[n][c] == S.zip(p).sumOf { (a, b) -> a * b })
            // return S

            return m[n][c]
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