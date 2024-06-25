package main.kotlin

import kotlin.math.pow
import kotlin.random.Random


class AntsColony(private val numItems: Int,
                 private val capacity: Int,
                 private val weights: IntArray,
                 private val profits: IntArray,
                 private val numAnts: Int = 100) {

    private val ks = SearchUtils(numItems, capacity, weights, profits)

    private val pheromoneImportance: Int = 2
    private val attractivenessImportance: Int = 3
    private val evaporationRate = 0.05
    private val maxIterations = 1200

    private fun calcAttractiveness(index: Int): Double {
        return profits[index] / weights[index].toDouble() // MINE
        // return profits[index] / weights[index].toDouble().pow(2) // AKA 2
        // return (profits[index] * capacity / weights[index]).toDouble() // AKA 3
    }

    private fun calcDirectProb(pheromone: Double, attractiveness: Double): Double {
        return pheromone.pow(pheromoneImportance) * attractiveness.pow(attractivenessImportance)
    }

    private fun calcProb (objects: MutableSet<Int>, pheromones: DoubleArray): DoubleArray {
        val attractiveness: DoubleArray = DoubleArray(numItems) { index ->
            if (index in objects) calcAttractiveness(index) else 0.0
        }

        val sum: Double = attractiveness.indices.sumOf { calcDirectProb(pheromones[it], attractiveness[it]) }
        return DoubleArray(numItems) { calcDirectProb(pheromones[it], attractiveness[it]) / sum }
    }

    private fun getSample (probabilities: DoubleArray, availableObjects: MutableSet<Int>): Int {
        val acceptance = Random.nextDouble(0.0, 1.0)
        var sum: Double = 0.0

        for (i in availableObjects) {
            sum += probabilities[i]
            if (acceptance <= sum) return i
        }

        throw Error("Probabilities doesn't add to 1")
    }

    private fun calcPheromoneDelta(bestFitness: Int, curFitness: Int): Double {
        return (1 / (1 + (bestFitness - curFitness) / bestFitness)).toDouble()
    }

    private fun evaporatePheromone(pheromone: Double): Double {
        return pheromone * (1 - evaporationRate)
    }

    private fun updatePheromones(pheromones: DoubleArray, bestFitness: Int, solutions: IntArray): DoubleArray {
        var nextPheromones: DoubleArray = DoubleArray(numItems)

        for (fitness in solutions) {
            val delta = calcPheromoneDelta(bestFitness, fitness)
            nextPheromones = pheromones.onEach { pheromone -> evaporatePheromone(pheromone) + delta }
        }

        return nextPheromones
    }

    fun solve (): IntArray {
        var bestSolution: IntArray = IntArray(numItems) { 0 }
        var pheromones: DoubleArray = DoubleArray(numItems) { 1.0 }

        // Run until time is reached
        val endTime = System.currentTimeMillis() + TIME_LIMIT_MS
        var iterations = MAX_ITERATIONS
        while(iterations > 0) {
            if (System.currentTimeMillis() > endTime) break

            val antsSolutions = IntArray(numAnts)

            // For every Ant
            for (i in 0 until numAnts) {
                val availableObjects: MutableSet<Int> = (0 until numItems).toMutableSet()

                var currentLoad = capacity
                val solution: IntArray = IntArray(numItems) {0}

                while (currentLoad > 0 && availableObjects.size > 0) {
                    val probabilities: DoubleArray = calcProb(availableObjects, pheromones)
                    val selection = getSample(probabilities, availableObjects)

                    // Update partial solution
                    solution[selection] = 1
                    currentLoad -= weights[selection]
                    availableObjects.remove(selection)
                    availableObjects.removeIf { weights[it] > currentLoad }
                }

                antsSolutions[i] = ks.calculateFitness(solution)
                if (ks.calculateFitness(bestSolution) < antsSolutions[i]) bestSolution = solution
            }

            // Update pheromones after every ant retrieved its solution
            pheromones = updatePheromones(pheromones, ks.calculateFitness(bestSolution), antsSolutions)

            iterations--
        }

        return bestSolution
    }

}