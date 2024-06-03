package main.kotlin

class LocalSearchKnapsack(private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val n: Int, private val neighborhood: Int = 0) {

	private val ks = SearchUtils(n, capacity, weights, profits);

	fun localSearch(timeLimit: Long = 60000, greedy: Boolean = false): IntArray {
		val end = System.currentTimeMillis() + timeLimit
		var bestSolution = if (greedy) ks.generateGreedySolution() else ks.generateRandomSolution()

		var currentSolution = IntArray(n) { 0 }

		while (currentSolution != bestSolution) {

			currentSolution = bestSolution

			for (neighbor in neighbors(currentSolution)) {

				if (ks.isValidSolution(neighbor) && ks.calculateFitness(neighbor) > ks.calculateFitness(bestSolution)) {
					bestSolution = neighbor
					break
				}

				if (System.currentTimeMillis() > end) {
					break
				}
			}

			if (System.currentTimeMillis() > end) {
				break
			}
		}

		return bestSolution
	}

	private fun neighbors(solution: IntArray): Sequence<IntArray> {
		return when (neighborhood) {
			0 -> swapNeighborhood(solution)
			1 -> flipNeighborhood(solution)
			else -> swapNeighborhood(solution)
		}
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
