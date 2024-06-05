package main.kotlin

class LocalSearchKnapsack(private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val n: Int, private val neighborhood: Int = 0) {

	private val ks = SearchUtils(n, capacity, weights, profits);

	fun localSearch(start: IntArray = intArrayOf(-1), greedy: Boolean = false): IntArray {
		val endTime = System.currentTimeMillis() + TIME_LIMIT_MS

		var bestSolution = start
		if (bestSolution[0] == -1) {
			bestSolution = if (greedy) ks.generateGreedySolution() else ks.generateRandomSolution()
		}
		var currentSolution = IntArray(n) { 0 }

		for (i in 0 until MAX_ITERATIONS) {

			currentSolution = bestSolution

			for (neighbor in neighbors(currentSolution)) {

				if (ks.isValidSolution(neighbor) && ks.calculateFitness(neighbor) > ks.calculateFitness(bestSolution)) {
					bestSolution = neighbor
					break
				}

				if (System.currentTimeMillis() > endTime) {
					return bestSolution
				}
			}

			if (currentSolution == bestSolution) {
				break
			}
		}

		return bestSolution
	}

	fun neighbors(solution: IntArray): Sequence<IntArray> {
		return when (neighborhood) {
			0 -> swapNeighborhood(solution)
			1 -> flipNeighborhood(solution)
			else -> swapNeighborhood(solution)
		}
	}

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

	private fun swapNeighborhood(solution: IntArray): Sequence<IntArray> = sequence {
		for (i in solution.indices) {
			for (j in i + 1 until solution.size) {
				val neighbor = solution.copyOf()
				neighbor[i] = solution[j]
				neighbor[j] = solution[i]
				yield(neighbor)
			}
		}
	}

	private fun flipNeighborhood(solution: IntArray): Sequence<IntArray> = sequence {
		for (i in solution.indices) {
			val neighbor = solution.copyOf()
			neighbor[i] = if (neighbor[i] == 0) 1 else 0
			yield(neighbor)
		}
	}

	fun isValidSolution(solution: IntArray): Boolean {
		return solution.zip(weights).sumOf { (a, b) -> a * b } <= capacity
	}

	fun calculateFitness(solution: IntArray): Int {
		return solution.zip(profits).sumOf { (a, b) -> a * b }
	}

}

// Usage example
fun main() {
	val capacity = 10
	val w = intArrayOf(2, 3, 5, 7)
	val p = intArrayOf(10, 15, 20, 25)

	val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, w.size)
	val bestSolution = localSearchKnapsack.localSearch()
	println("Best solution: ${bestSolution.toList()}")
	println("Total weight: ${bestSolution.zip(w).sumOf { (a, b) -> a * b }}")
	println("Total profit: ${bestSolution.zip(p).sumOf { (a, b) -> a * b }}")
}
