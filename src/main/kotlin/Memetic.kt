package main.kotlin


import java.util.*

class GeneticMemetic (private val n: Int, private val capacity: Int, private val weights: IntArray, private val profits: IntArray, private val populationSize: Int = 100) {

    private val crossoverRange = 0.45..0.65
    private val mutationRate = 0.1
    //private val weights = weights
   // private val capacity = capacity
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

        var child: IntArray = firstGenotype.slice(0 until genotypeCutAt).toIntArray()
        child += secondGenotype.slice(genotypeCutAt  until secondGenotype.size).toIntArray()

        return child
    }

    private fun crossover2(genotype: MutableList<IntArray>): IntArray {
        val crossoverRate = Random().nextDouble() * (crossoverRange.endInclusive - crossoverRange.start) + crossoverRange.start

        var child: MutableList<Int> = mutableListOf()

        val sizeBag = weights.size
        var suma = 0
        for (i in 0..sizeBag-1){
            var change = false
            var changedTo = 0
            for (j in 0..genotype.size-1){
                for (k in 0..genotype.size-1){
                    if (j!=k && genotype[k].size!=0 && genotype[j].size!=0){
                        val first = genotype[j][i]
                        val second = genotype[k][i]
                        if (first == second){
                            suma+=weights[i]
                            change=true
                            changedTo = first
                        }
                        else{
                            if (first == 1 && weights[i]+suma < capacity && second==0){
                                changedTo = first
                            }
                            else if (second == 1 && weights[i]+suma < capacity && first==0){
                                changedTo = second
                            }
                            else{
                                changedTo = 0
                            }
                        }
                    }
                }
            }
        }
        var sumaa=0
        for (f in 0..child.size-1){
            if (child[f]==1){
                sumaa+=weights[f]
            }
        }
        if (sumaa<=capacity) {
            return child.toIntArray()
        }
        return genotype.maxBy { ks.calculateFitness(it) }

        //var child: IntArray = firstGenotype.slice(0 until genotypeCutAt).toIntArray()
        //child += secondGenotype.slice(genotypeCutAt  until secondGenotype.size).toIntArray()
        //return child.toIntArray()
    }

    private fun mutate(genotype: IntArray): IntArray {
        for (i in genotype.indices) {
            if (Random().nextDouble() < mutationRate) genotype[i] = genotype[i] xor 1
        }
        return genotype
    }

    private fun getChildren (population: MutableList<IntArray>): List<IntArray> {
        val children = mutableListOf<IntArray>()
        while (population.size > 1) {
            var genotype : MutableList<IntArray> = mutableListOf()
            for (i in 0..(population.size/2)) {
                val firstParent = population.random()
                population.remove(firstParent)
                genotype.add(firstParent)
            }
            /*val firstParent = population.random()
            val secondParent = population.random()
            population.remove(firstParent)
            population.remove(secondParent)
            genotype.add(firstParent)
            genotype.add(secondParent)*/
            var fchild = crossover2(genotype)
            while (!ks.isValidSolution(fchild)) fchild = (crossover2(genotype))
            children.add(fchild)
            var firstChild = mutate(crossover2(genotype))
            while (!ks.isValidSolution(firstChild)) firstChild = mutate(crossover2(genotype))
            children.add(firstChild)

            //var secondChild = mutate(crossover2(genotype))
            //while (!ks.isValidSolution(secondChild)) secondChild = mutate(crossover2(genotype))
            //children.add(secondChild)
        }

        children.addAll(population)

        return children
    }

    private fun getNeighbors(population: MutableList<IntArray>) : MutableList<IntArray>{
        var neighbors : MutableList<IntArray> = mutableListOf()
        for (p in population){
            var neighbor = p.clone()
            for (x in 0..p.size-1){
                if (p[x]==0){
                    neighbor[x] = 1
                    if (ks.isValidSolution(neighbor)){
                        neighbors.add(neighbor)
                    }
                }
                else{
                    neighbor[x] = 0
                    if (ks.isValidSolution(neighbor)){
                        neighbors.add(neighbor)
                    }
                }
            }
        }
        return neighbors
    }

    private fun thresholdSearch(population: MutableList<IntArray>) : IntArray{
        //https://arxiv.org/pdf/2101.04753v1
        var S = population.maxBy { ks.calculateFitness(it) }
        var Sb = population.maxBy { ks.calculateFitness(it) }
        var threshold = ks.calculateFitness(S)
        var iterMax = 20
        var i=0
        var neighbors : MutableList<IntArray> = getNeighbors(population)
        while (i < iterMax){
            for (n in neighbors){
                if (ks.calculateFitness(n) <= threshold) {
                    S = n
                }
            }
            if (ks.calculateFitness(S) < ks.calculateFitness(Sb)){
                Sb=S
            }
            i+=1
            neighbors = getNeighbors(mutableListOf(Sb) )
        }
        return Sb
    }

    fun solve(): IntArray {
        var population: MutableList<IntArray> = MutableList(populationSize) { ks.generateRandomSolution() }
        var bestSolution = population.maxBy { ks.calculateFitness(it) }
        var generations = 0
        var lastGenerationForBestSolution = 0
        val endTime = System.currentTimeMillis() + TIME_LIMIT_MS

        while (true) {
            if (System.currentTimeMillis() > endTime) break
            if (generations - lastGenerationForBestSolution > 1000) break

            val parents = getParents(population) // Parents compete
            val children = getChildren(parents.toMutableList()) // Parents crossover and children mutation
            population.addAll(children) // New population
            population = population.shuffled().toMutableList()

            val populationBest = thresholdSearch(population) //population.maxBy { ks.calculateFitness(it) }
            if (ks.calculateFitness(bestSolution) < ks.calculateFitness(populationBest)) {
                bestSolution = populationBest
                population = mutableListOf(populationBest)
                lastGenerationForBestSolution = generations
            }

            generations++
        }

        return bestSolution
    }
}

fun main() {
    val capacity = 10
    val w = intArrayOf(2, 3, 5, 7)
    val p = intArrayOf(10, 15, 20, 25)
    val g = GeneticMemetic(4, capacity,w, p)
    println(g.solve().toMutableList())
}