package main.kotlin

import kotlin.math.max

enum class Algorithms {
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

        override fun isActive(): Boolean {
            return true
        }
    },

    KNAPSACK_HEURISTICS {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            var solution = 0
            var currentCapacity = c

            val sortedItems = (0 until n).sortedByDescending { p[it] / w[it] }.toMutableList()
            while (currentCapacity > 0 && sortedItems.isNotEmpty()) {
                val item = sortedItems.first()
                if (w[item] <= currentCapacity) {
                    solution += p[item]
                    currentCapacity -= w[item]
                }
                sortedItems.removeFirst()
            }

            return solution
        }

        override fun isActive(): Boolean {
            return false
        }
    },

    KNAPSACK_LOCAL_SEARCH_SWAP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n)
            val sol = localSearchKnapsack.localSearch(59000)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }

        override fun isActive(): Boolean {
            return false
        }
    },

    KNAPSACK_LOCAL_SEARCH_FLIP {

        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, 1)
            val sol = localSearchKnapsack.localSearch(59000)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }

        override fun isActive(): Boolean {
            return false
        }
    },

    KNAPSACK_GENETIC {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val genetic = Genetic(n, c, w, p, 1000)
            val sol = genetic.solve()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }

        override fun isActive(): Boolean {
            return true
        }
    };

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
    abstract fun isActive(): Boolean
}