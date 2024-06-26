package main.kotlin
import kotlin.math.max

var TIME_LIMIT_MS = 120_000
var MAX_ITERATIONS = 10_000
var MAX_ITER_WITHOUT_IMPROVE = 10
var SHORT_MAX_ITERATIONS = 1000

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
            val localSearchKnapsack = LocalSearchKnapsack(c, w, p, n, neighborhood = "flip")
            val sol = localSearchKnapsack.localSearch()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_ITERATIVE_LOCAL_SEARCH {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val knapsackILS = IterativeLocalSearch(c, w, p, n)
            val sol = knapsackILS.iterativeLocalSearch()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_TABU_SEARCH {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val tabuSearch = TabuSearch(c, w, p, n)
            val sol = tabuSearch.tabuSearch()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_SIMULATED_ANNEALING {
      override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
          val sa = SimulatedAnnealing(c, w, p, n)
          val sol = sa.simulatedAnnealing()
          return sol.zip(p).sumOf { (a, b) -> a * b }
      }
    },

    KNAPSACK_GENETIC {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val genetic = Genetic(n, c, w, p, 1000)
            val sol = genetic.solve()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_GRASP {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val graspKnapsack = GraspKnapsack(c, w, p, n)
            val sol = graspKnapsack.grasp()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_MEMETIC {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val ants = GeneticMemetic(n, c, w, p, 10)
            val sol = ants.solve()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_SCATTER_SEARCH {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val scatterSearch = ScatterSearch(c, w, p, n)
            val sol = scatterSearch.scatterSearch(populationSize = 20, referenceSize = 10)
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    },

    KNAPSACK_ANTS {
        override fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int {
            val ants = AntsColony(n, c, w, p, 10)
            val sol = ants.solve()
            return sol.zip(p).sumOf { (a, b) -> a * b }
        }
    };

    abstract fun solve(c: Int, w: IntArray, p: IntArray, n: Int): Int
}