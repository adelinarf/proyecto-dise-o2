package main.kotlin

import java.util.*


class TabuList<T>(private val maxSize: Int) {
    private val list = LinkedList<T>()

    fun add(element: T) {
        if (list.size == maxSize) list.removeFirst()
        list.addLast(element)
    }

    fun get(): List<T> {
        return list
    }

    operator fun contains(pair: T): Boolean {
        return pair in list
    }
}

class TabuSearch(private val capacity: Int, private val w: IntArray, private val p: IntArray, private val n: Int) {
    private val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, n)
    private var endTime = 0L

    fun best_VA(neighborhood : Sequence<IntArray>,S : IntArray) : IntArray {
        return neighborhood.maxByOrNull { localSearchKnapsack.calculateFitness(it) }  ?: intArrayOf()
    }

    fun remove(neighborhood : Sequence<IntArray>,y:IntArray) : Sequence<IntArray>{
        var m = mutableListOf<IntArray>()
        for (x in neighborhood){
            if (x contentEquals y){
                continue
            }
            else{
                m.add(x)
            }
        }
        return m.asSequence()
    }

    fun add(neighborhood : Sequence<IntArray>,y:IntArray) : Sequence<IntArray>{
        var m = mutableListOf<IntArray>()
        for (x in neighborhood){
            m.add(x)
        }
        m.add(y)
        return m.asSequence()
    }

    fun swapTabu(solution : IntArray, outside : MutableList<Int>) = sequence {
        for (i in solution.indices) {
            for (j in 1 .. outside.size-1) {
                val neighbor = solution.copyOf()
                neighbor[i] = solution[outside[j]]
                neighbor[outside[j]] = solution[i]
                yield(neighbor)
            }
        }
    }

    fun swapTabu2(solution: IntArray, inside: MutableList<Int>, outside: MutableList<Int>, tabu: TabuList<Pair<Int, Int>>) = sequence {
        val maxSwapsAllowed = 1

        var swaps = 0
        while (inside.isNotEmpty() && outside.isNotEmpty() && swaps < maxSwapsAllowed) {
            var pair: Pair<Int, Int>
            do pair = Pair(inside.random(), outside.random())
            while (pair in tabu)

            val neighbor = solution.copyOf()
            neighbor[pair.first] = solution[pair.second]
            neighbor[pair.second] = solution[pair.first]

            inside.remove(pair.first)
            outside.remove(pair.second)

            swaps++
            yield(Pair(pair, neighbor))
        }
    }

    fun updateTabu(S: IntArray, tabu: TabuList<Pair<Int, Int>>): Sequence<IntArray> = sequence {
        val inside = mutableListOf<Int>()
        val outside = mutableListOf<Int>()
        for (x in 1 until S.size) { if (S[x] == 1) inside.add(x) else outside.add(x) }
        val seq = swapTabu2(S, inside, outside, tabu)
        val pair = seq.first().first
        val neighbor = seq.first().second

        if (!tabu.get().contains(pair) && !localSearchKnapsack.isValidSolution(neighbor)) {
            tabu.add(pair)
            yield(neighbor)
        }

        /*for ((pair, neighbor) in seq) {
            if (tabu.get().contains(pair)) continue
            if (!localSearchKnapsack.isValidSolution(neighbor)) println("INVALIDA")
            //if (localSearchKnapsack.isValidSolution(neighbor)) {
            tabu.add(pair)
            yield(neighbor)
            //}
            // println("Tabu: ${neighbor.contentToString()}")
        }*/


        //return seq.map { it.second }


        //var neighborhood = localSearchKnapsack.neighbors(S)
        //var counter = 0
        /*for (y in neighborhood){
            if (System.currentTimeMillis() > endTime){
                break
            }
            var suma=0
            for (x in 0..y.size-1){
                if (y[x] == 1){
                    suma+=w[x]
                }
            }
            if (suma > capacity){
                neighborhood = remove(neighborhood,y)
                Tabu.add(y)
            }
            counter+=1
        }*/
        // Tabu.add(S) // Debe guardar el movimiento no la solucion
        /*for (x in neighborhood){
            if (x contentEquals S){
                neighborhood = remove(neighborhood,x)
            }
        }*/
        //Criterio de aceptacion, si esta en Tabu pero logra un mejor resultado, se agrega a la vecindad
        /*for (x in Tabu){
            if (System.currentTimeMillis() > endTime){
                break
            }
            if (localSearchKnapsack.calculateFitness(x) > localSearchKnapsack.calculateFitness(S)){
                neighborhood = add(neighborhood,x)
            }
        }*/
    }

    fun tabuSearch() : IntArray{
        var S_0 = localSearchKnapsack.generateGreedySolution()
        var S_p = S_0.copyOf()
        var Tabu = TabuList<Pair<Int, Int>>(n / 10)
        var VA = updateTabu(S_0, Tabu)       // veccindad aceptable que no es tabu
        VA.forEach { if (!localSearchKnapsack.isValidSolution(it)) println("INVALIDA") }
        endTime = System.currentTimeMillis() + TIME_LIMIT_MS
        for (i in 0 until MAX_ITERATIONS) {
            if (System.currentTimeMillis() > endTime){
                break
            }
            S_p = best_VA(VA,S_p)
            VA = updateTabu(S_p,Tabu)
            //println(Tabu.get())
            if (localSearchKnapsack.calculateFitness(S_p) > localSearchKnapsack.calculateFitness(S_0)){
                S_0 = S_p.copyOf()
            }
        }
        return S_p

    }
}