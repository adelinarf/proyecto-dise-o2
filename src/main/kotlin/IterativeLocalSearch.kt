package main.kotlin

class IterativeLocalSearch(private val capacity: Int, private val w: IntArray, private val p: IntArray, private val n: Int) {
    private val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, n)

    fun criterio_aceptacion(best_Sol: IntArray, next_Best: IntArray, memoria: MutableList<IntArray>): IntArray {
        val a = localSearchKnapsack.calculateFitness(best_Sol)
        val b = localSearchKnapsack.calculateFitness(next_Best)
        if (a > b) {
            if (!inMutableList(best_Sol, memoria)) {
                memoria.add(best_Sol)
                return best_Sol
            }
            return best_Sol
        }
        if (!inMutableList(next_Best, memoria)) {
            memoria.add(next_Best)
            return next_Best
        }
        return best_Sol
    }

    fun inMutableList(e: IntArray, m: MutableList<IntArray>): Boolean {
        var contiene = false
        for (x in m) {
            val z = x contentEquals e
            contiene = contiene || z
        }
        return contiene
    }

    fun perturbacion(originalS: IntArray, memoria: MutableList<IntArray>): IntArray {
        //sacar al mas grande y agregarle los chicos que sumen su cantidad y que no hayan sido tomados
        val S = originalS.copyOf()
        val weights = w.copyOf()
        var maximum = 0
        while (maximum != -1) {
            maximum = weights.maxOrNull() ?: -1
            if (maximum == -1) {
                break
            }
            var index = weights.indexOf(maximum)
            if (S[index] == 1) {
                var sumados = 0
                var conseguidos: MutableList<Int> = mutableListOf()
                for (x in 0..S.size - 1) {
                    if (S[x] == 0 && (weights[x] + sumados) <= weights[index] && (weights[x] + sumados) <= capacity && weights[x] != -1) {
                        sumados += weights[x]
                        conseguidos.add(x)
                    }
                }
                if (conseguidos.size >= 1) {
                    S[index] = 0
                    for (y in 0..conseguidos.size - 1) {
                        S[conseguidos[y]] = 1
                    }
                }
                weights[index] = -1
            } else {
                weights[index] = -1
            }
        }
        if (localSearchKnapsack.calculateFitness(S) > localSearchKnapsack.calculateFitness(originalS)) {
            if (!inMutableList(S, memoria)) {
                memoria.add(S)
                return S
            }
            return originalS
        }
        return originalS
    }

    fun iterativeLocalSearch(): IntArray {
        val S_0 = localSearchKnapsack.generateGreedySolution()
        var S_p = localSearchKnapsack.localSearch(S_0)

        val memoria: MutableList<IntArray> = mutableListOf()

        val endTime = System.currentTimeMillis() + TIME_LIMIT_MS

        for (i in 0 until MAX_ITERATIONS) {

            if (System.currentTimeMillis() > endTime) {
                break
            }

            val Sp = perturbacion(S_p, memoria);
            val Spp = localSearchKnapsack.localSearch(Sp);
            S_p = criterio_aceptacion(S_p, Spp, memoria)
        }
        return S_p;
    }
}