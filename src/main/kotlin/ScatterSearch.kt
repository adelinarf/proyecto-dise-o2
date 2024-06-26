package main.kotlin
class ScatterSearch(private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val n: Int, private val neighborhood: String = "flip") {
	private val grasp = GraspKnapsack(capacity, weights, profits, n)
	private val ks = LocalSearchKnapsack(capacity, weights, profits, n)
	private var bestSolution = IntArray(n) { 0 }
	private var bestValue = 0
	private var endTime = Long.MAX_VALUE
	private var iterationsWithoutImprovement = 0
	
	fun fitness(solution: IntArray): Int {
		if (!ks.isValidSolution(solution)) return 0
		return ks.calculateFitness(solution)
	}

	fun generateInitialPopulation(populationSize: Int): List<IntArray> {
		val population = mutableListOf<IntArray>()
		for (i in 0 until populationSize) {
			population.add(grasp.generateRandomizedGreedySolution())
		}
		return population
	}

	fun diversitySelection(population: List<IntArray>, referenceSize: Int): List<IntArray> {
		// ensure diversity, more distance == more diverse
		val referenceSet = mutableListOf<IntArray>()
		val populationSet = population.toMutableList()
		while (referenceSet.size < referenceSize) {
			if (System.currentTimeMillis() > endTime) return population

			val mostDistant = populationSet.maxByOrNull { solution -> referenceSet.sumOf { distance(it, solution) } }
			referenceSet.add(mostDistant!!)
			populationSet.remove(mostDistant)
		}
		return referenceSet
	}

	fun recombination(referenceSet: List<IntArray>): List<IntArray> {
		// Using score based recombination for all subsets of size 3, 4 and 5
		val newPopulation = mutableListOf<IntArray>()

		for (subsetSize in 3..5) {
			if (System.currentTimeMillis() > endTime) return referenceSet

			val subsets = referenceSet.combinations(subsetSize)
			for (subset in subsets) {
				newPopulation.add(scoreBasedCombination(subset))
			}
		}

		return newPopulation
	}

	fun scoreBasedCombination(subset: List<IntArray>): IntArray {
		val scores = IntArray(n) { 0 }
		val totalProfit = subset.sumOf { fitness(it) }

		// If all solutions have 0 profit, return a random solution
		if (totalProfit == 0) return IntArray(n) { (0..1).random() }

		for (j in 0 until n) {
			for (solution in subset) {
				if (System.currentTimeMillis() > endTime) {
					return IntArray(n) { 0 }
				}
				val xj = solution[j]
				scores[j] += fitness(solution) * xj + (1 - 2*xj)*profits[j]
			}
			scores[j] /= totalProfit
		}

		val newSolution = IntArray(n) { 0 }
		for (j in 0 until n) {
			newSolution[j] = if (scores[j] > 0.5) 1 else 0
		}
		return newSolution
	}

	fun pathRelinking(initialSolution: IntArray, targetSolution: IntArray) : List<IntArray> {
		val path = mutableListOf<IntArray>()
		val currentSolution = initialSolution.copyOf()

		for (i in 0 until n) {
			if (System.currentTimeMillis() > endTime) break

			if (currentSolution[i] != targetSolution[i]) {
				currentSolution[i] = targetSolution[i]
				val improvedSolution = ks.localSearch(currentSolution, endTime = endTime, maxIterations = SHORT_MAX_ITERATIONS)

				val currentValue = fitness(improvedSolution)
				if (currentValue > bestValue) {
					bestValue = currentValue
					bestSolution = improvedSolution.copyOf()
					iterationsWithoutImprovement = 0
				}
				path.add(improvedSolution)
			}
		}

		return path
	}

	fun distance(solution1: IntArray, solution2: IntArray): Int {
		var diff = 0
		for (i in 0 until n) {
			if (solution1[i] != solution2[i]) diff++
		}
		return diff
	}

	fun dichotomousGenerator(): List<IntArray> {
		val initialSolution = grasp.generateRandomizedGreedySolution()
		val population = mutableListOf<IntArray>()
		complementSolution(population, initialSolution, (0 until n/2).toList())
		complementSolution(population, initialSolution, (n/2 until n).toList())
		return population
	}

	fun complementSolution(population: MutableList<IntArray>, solution: IntArray, indices: List<Int>): Unit {
		if (indices.isEmpty()) return
		val complimentarySolution = solution.copyOf()
		for (i in indices) {
			complimentarySolution[i] = 1 - complimentarySolution[i]
		}
		population.add(complimentarySolution)
		if (indices.size == 1) return
		complementSolution(population, solution, indices.subList(0, indices.size / 2))
		complementSolution(population, solution, indices.subList(indices.size / 2, indices.size))
	}

	fun scatterSearch(populationSize: Int = 10, referenceSize: Int = 5, maxIterations: Int = -1, endTime: Long = -1): IntArray {
		this.endTime = if (endTime == -1L) System.currentTimeMillis() + TIME_LIMIT_MS else endTime
		val iterLimit = if (maxIterations == -1) MAX_ITERATIONS else maxIterations

		// Generate initial population, improve each solution with local search
		var initialPopulation = generateInitialPopulation(populationSize).toMutableList()
		var referenceSet = mutableListOf<IntArray>()
		for (solution in initialPopulation) {
			val improvedSolution =
				ks.localSearch(solution, endTime = this.endTime, maxIterations = SHORT_MAX_ITERATIONS)
			referenceSet.add(improvedSolution)
		}

		// Find the best solution in reference set
		bestSolution = referenceSet.maxByOrNull { fitness(it) }!!
		bestValue = fitness(bestSolution)

		// Diversity selection to ensure diverse reference set size and diversity
		referenceSet = diversitySelection(referenceSet, referenceSize).toMutableList()

		// Scatter search main loop
		for (i in 0 until iterLimit) {
			if (System.currentTimeMillis() > this.endTime) break

			// Recombination and diversity selection
			val newPopulation = recombination(referenceSet)
			val bestLocal = newPopulation.maxByOrNull { fitness(it) }!!
			referenceSet = diversitySelection(newPopulation, referenceSize).toMutableList()

			// Path relinking with the best local solution
			val path = pathRelinking(referenceSet.random(), bestLocal)
			referenceSet.addAll(path)

			// Update best solution
			val bestLocalValue = fitness(bestLocal)
			if (bestLocalValue > bestValue) {
				bestValue = bestLocalValue
				bestSolution = bestLocal.copyOf()
				iterationsWithoutImprovement = 0
			} else {
				iterationsWithoutImprovement++

				// Restart if no improvement
				if (iterationsWithoutImprovement > MAX_ITER_WITHOUT_IMPROVE) {
					initialPopulation = generateInitialPopulation(populationSize).toMutableList()
					referenceSet = mutableListOf<IntArray>()
					for (solution in initialPopulation) {
						val improvedSolution =
							ks.localSearch(solution, endTime = this.endTime, maxIterations = SHORT_MAX_ITERATIONS)
						referenceSet.add(improvedSolution)
					}
				}
			}

			// Diversity selection to ensure diverse reference set size and diversity
			referenceSet = diversitySelection(referenceSet, referenceSize).toMutableList()
		}

		return bestSolution
	}
}

fun <T> List<T>.combinations(n: Int): List<List<T>> {
    if (n == 0) return listOf(emptyList())
    if (isEmpty()) return emptyList()

    val element = first()
    val rest = drop(1)
    val withoutFirst = rest.combinations(n)
    val withFirst = rest.combinations(n - 1).map { combination -> combination + element }

    return withoutFirst + withFirst
}

fun main() {
	val capacity = 10
	val weights = intArrayOf(2, 3, 4, 5, 6)
	val profits = intArrayOf(3, 4, 5, 6, 7)
	val n = weights.size
	val scatterSearch = ScatterSearch(capacity, weights, profits, n)
	val solution = scatterSearch.scatterSearch()
	println("Best solution: ${solution.contentToString()}")
}