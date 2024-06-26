package main.kotlin
class GraspKnapsack(private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val n: Int, private val neighborhood: Int = 0) {
	
	private val ks = SearchUtils(n, capacity, weights, profits)

	fun grasp(alpha: Double = 0.5, maxIterations: Int = -1): IntArray {
		val endTime = System.currentTimeMillis() + TIME_LIMIT_MS
		val iterLimit = if (maxIterations > 0) maxIterations else MAX_ITERATIONS

		var bestSolution = generateRandomizedGreedySolution(alpha)
		var bestValue = ks.calculateFitness(bestSolution)
		var iterationsWithoutImprovement = 0
		val ls = LocalSearchKnapsack(capacity, weights, profits, n)

		for (i in 0 until iterLimit) {
			val initialSolution = generateRandomizedGreedySolution()
			val localOptimum = ls.localSearch(initialSolution)
			val localValue = ks.calculateFitness(localOptimum)

			if (localValue > bestValue) {
				bestSolution = localOptimum
				bestValue = localValue
				iterationsWithoutImprovement = 0
			} else {
				iterationsWithoutImprovement++
			}

			if (iterationsWithoutImprovement > MAX_ITER_WITHOUT_IMPROVE) {
				break
			}

			if (System.currentTimeMillis() > endTime) {
				break
			}
		}

		return bestSolution
	}

	fun generateRandomizedGreedySolution(alpha: Double = 0.5): IntArray {
		var solution = IntArray(n) { 0 }
		var remainingCapacity = capacity

		// sort by profit/weight
		val sortedItems = (0 until n).sortedByDescending { profits[it] / weights[it].toDouble() }.toMutableList()
		while (remainingCapacity > 0 && sortedItems.isNotEmpty()) {
			val RCL = buildRCL(sortedItems, remainingCapacity, alpha)
			if (RCL.isEmpty()) break

			val i = RCL.random()
			val item = sortedItems[i]
			sortedItems.removeAt(i)

			solution[item] = 1
			remainingCapacity -= weights[item]
		}
		return solution
	}

	private fun buildRCL(
		sortedItems: MutableList<Int>,
		remainingCapacity: Int,
		alpha: Double = 0.5
	): List<Int> {
		val maxI = sortedItems.first()
		val minI = sortedItems.last()
		val max = profits[maxI] / weights[maxI].toDouble()
		val min = profits[minI] / weights[minI].toDouble()

		val threshold = max - alpha * (max - min)

		val RCL = mutableListOf<Int>()
		for (i in 0 until sortedItems.size) {
			val itemRate = profits[sortedItems[i]] / weights[sortedItems[i]].toDouble()
			if (itemRate >= threshold && weights[sortedItems[i]] <= remainingCapacity) {
				RCL += i
			} else {
				break
			}
		}
		return RCL
	}
}


fun main() {
	val capacity = 10
	val weights = intArrayOf(3, 4, 5, 2)
	val profits = intArrayOf(4, 5, 6, 3)
	val size = weights.size

	val gk = GraspKnapsack(capacity, weights, profits, size)
	val solution = gk.grasp()
	println("Solution: ${solution.toList()}")
}