package main.kotlin

class SearchUtils (private val n: Int, private val capacity: Int, private val weights: IntArray, private val profits: IntArray) {

    fun generateRandomSolution(): IntArray {
        val solution = IntArray(n) { 0 }
        var remainingCapacity = capacity

        for (i in weights.indices.shuffled()) {
            if (remainingCapacity >= weights[i]) {
                solution[i] = 1
                remainingCapacity -= weights[i]
            }
        }

        return solution
    }

    fun generateGreedySolution(): IntArray {
        val solution = IntArray(n) { 0 }
        var remainingCapacity = capacity

        for ((i, _) in profits.sortedByDescending { it }.withIndex()) {
            if (weights[i] <= remainingCapacity) {
                solution[i] = 1
                remainingCapacity -= weights[i]
            }
        }

        return solution
    }

    fun isValidSolution(solution: IntArray): Boolean {
        return solution.zip(weights).sumOf { (a, b) -> a * b } <= capacity
    }

    fun calculateFitness(solution: IntArray): Int {
        return solution.zip(profits).sumOf { (a, b) -> a * b }
    }

}
