data class Item(val weight: Int, val value: Int)

class LocalSearchKnapsack(private val items: List<Item>, private val capacity: Int) {

	fun localSearch(iterations: Int): List<Item> {
		var bestSolution = generateRandomSolution()
		// var bestSolution = generateGreedySolution()

		var currentSolution = IntArray(items.size) { 0 }
		var iter = 0

		while (currentSolution != bestSolution && iter++ < iterations) {
			println("Iteration $iter: Best solution: ${bestSolution.toList()} - Fitness: ${calculateFitness(bestSolution)}")
			currentSolution = bestSolution

			for (neighbor in swapNeighborhood(currentSolution)) {
				if (isValidSolution(neighbor) && calculateFitness(neighbor) > calculateFitness(bestSolution)) {
					bestSolution = neighbor
					break
				}
			}
		}

		return bestSolution.indices.filter { bestSolution[it] == 1 }.map { items[it] }
	}

	private fun generateRandomSolution(): IntArray {
		val solution = IntArray(items.size) { 0 }
		var remainingCapacity = capacity

		for (i in items.indices.shuffled()) {
			if (remainingCapacity >= items[i].weight) {
				solution[i] = 1
				remainingCapacity -= items[i].weight
			}
		}

		return solution
	}

	private fun generateGreedySolution(): IntArray {
		val solution = IntArray(items.size) { 0 }
		var remainingCapacity = capacity

		for ((index, item) in items.sortedByDescending { it.value }.withIndex()) {
			if (item.weight <= remainingCapacity) {
				solution[index] = 1
				remainingCapacity -= item.weight
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

	private fun isValidSolution(solution: IntArray): Boolean {
		return solution.indices.sumOf { solution[it] * items[it].weight } <= capacity
	}

	private fun calculateFitness(solution: IntArray): Int {
		return solution.indices.sumOf { solution[it] * items[it].value }
	}
}

// Usage example
fun main() {
	val items = listOf(
		Item(2, 10),
		Item(3, 15),
		Item(5, 20),
		Item(7, 25)
	)
	val capacity = 10
	val iterations = 100

	val localSearchKnapsack = LocalSearchKnapsack(items, capacity)
	val bestSolution = localSearchKnapsack.localSearch(iterations)
	println("Best solution: $bestSolution")
	println("- Value: ${bestSolution.sumOf { it.value }}")
	println("- Weight: ${bestSolution.sumOf { it.weight }}")
}