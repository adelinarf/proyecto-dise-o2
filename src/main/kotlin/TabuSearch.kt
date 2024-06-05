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

    fun swapTabu(solution: IntArray, inside: MutableList<Int>, outside: MutableList<Int>, tabu: TabuList<Pair<Int, Int>>) = sequence {
        if (inside.isEmpty() || outside.isEmpty()) return@sequence

        val maxSwapsAllowed = 1
        var swaps = 0
        while (inside.isNotEmpty() && outside.isNotEmpty() && swaps < maxSwapsAllowed) {
            var pair: Pair<Int, Int>
            var neighbor: IntArray
            do {
                do pair = Pair(inside.random(), outside.random())
                while (pair in tabu)

                neighbor = solution.copyOf()
                neighbor[pair.first] = solution[pair.second]
                neighbor[pair.second] = solution[pair.first]
            } while (!localSearchKnapsack.isValidSolution(neighbor))

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
        val seq = swapTabu(S, inside, outside, tabu).firstOrNull()

        if (seq != null) {
            val pair = seq.first
            val neighbor = seq.second
            tabu.add(pair)
            yield(neighbor)
        } else return@sequence
    }

    fun tabuSearch() : IntArray{
        var S_0 = localSearchKnapsack.generateRandomSolution()
        var S_p = S_0.copyOf()
        var Tabu = TabuList<Pair<Int, Int>>((n * 0.05).toInt())
        var VA = updateTabu(S_0, Tabu)
        endTime = System.currentTimeMillis() + TIME_LIMIT_MS
        for (i in 0 until MAX_ITERATIONS) {
            if (System.currentTimeMillis() > endTime) break

            // if (VA.any()) S_p = best_VA(VA, S_p)
            VA = updateTabu(S_p,Tabu)
            if (localSearchKnapsack.calculateFitness(S_p) > localSearchKnapsack.calculateFitness(S_0)) S_0 = S_p.copyOf()
        }
        return S_p

    }
}