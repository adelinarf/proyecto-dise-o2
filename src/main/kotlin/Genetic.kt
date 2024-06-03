package main.kotlin

import java.util.*

class Genetic (private val n: Int, private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val populationSize: Int = 100) {

    private val crossoverRange = 0.45..0.65
    private val mutationRate = 0.1
    private val _timeLimit = 300000
    private val ks = SearchUtils(n, capacity, weights, profits)

    private fun compete(firstGenotype: IntArray, secondGenotype: IntArray) : IntArray {
        return if (ks.calculateFitness(firstGenotype) > ks.calculateFitness(secondGenotype)) firstGenotype else secondGenotype
    }

    private fun getParents(population: MutableList<IntArray>): List<IntArray> {
        val winners = mutableListOf<IntArray>()
        while (population.size > 1) {
            val firstCompetitor = population.random()
            val secondCompetitor = population.random()
            population.remove(firstCompetitor)
            population.remove(secondCompetitor)

            winners.add(compete(firstCompetitor, secondCompetitor))
        }

        // Add the remaining population
        winners.addAll(population)
        return winners
    }

    private fun crossover(firstGenotype: IntArray, secondGenotype: IntArray): IntArray {
        val crossoverRate = Random().nextDouble() * (crossoverRange.endInclusive - crossoverRange.start) + crossoverRange.start
        val genotypeCutAt: Int = (firstGenotype.size * crossoverRate).toInt()

        var child: IntArray = firstGenotype.slice(0..genotypeCutAt).toIntArray()
        child += secondGenotype.slice(genotypeCutAt + 1..secondGenotype.size).toIntArray()

        return child
    }

    private fun mutate(genotype: IntArray): IntArray {
        for (i in genotype.indices) {
            if (Random().nextDouble() < mutationRate) genotype[i] = if (genotype[i] == 0) 1 else 0
        }
        return genotype
    }

    private fun getChildren (population: MutableList<IntArray>): List<IntArray> {
        val children = mutableListOf<IntArray>()
        while (population.size > 1) {
            val firstParent = population.random()
            val secondParent = population.random()
            population.remove(firstParent)
            population.remove(secondParent)

            var firstChild = mutate(crossover(firstParent, secondParent))
            while (!ks.isValidSolution(firstChild)) firstChild = mutate(crossover(firstParent, secondParent))
            children.add(firstChild)

            var secondChild = mutate(crossover(secondParent, firstParent))
            while (!ks.isValidSolution(secondChild)) secondChild = mutate(crossover(secondParent, firstParent))
            children.add(secondChild)
        }

        return children
    }

    fun solve(timeLimit: Int = _timeLimit): IntArray {
        var population: MutableList<IntArray> = MutableList(populationSize) { ks.generateRandomSolution() }
        var bestSolution = population.maxBy { ks.calculateFitness(it) }
        var generations = 0
        var lastGenerationForBestSolution = 0

        while (true) {
            if (System.currentTimeMillis() > timeLimit) break
            if (generations - lastGenerationForBestSolution > 1000) break

            val parents = getParents(population) // Parents compete
            val children = getChildren(parents.toMutableList()) // Parents crossover and children mutation
            population.addAll(children) // New population
            population = population.shuffled().toMutableList()

            val populationBest = population.maxBy { ks.calculateFitness(it) }
            if (ks.calculateFitness(bestSolution) < ks.calculateFitness(populationBest)) {
                bestSolution = populationBest
                lastGenerationForBestSolution = generations
            }

            generations++
        }

        return bestSolution
    }
}