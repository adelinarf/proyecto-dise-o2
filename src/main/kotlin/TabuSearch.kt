package main.kotlin

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

    fun updateTabu(neighborhood : Sequence<IntArray>, S:IntArray, Tabu: MutableList<IntArray>) : Sequence<IntArray>{
        var neighborhood = localSearchKnapsack.neighbors(S)
        var counter = 0
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
        Tabu.add(S)
        for (x in neighborhood){
            if (x contentEquals S){
                neighborhood = remove(neighborhood,x)
            }
        }
        //Criterio de aceptacion, si esta en Tabu pero logra un mejor resultado, se agrega a la vecindad
        /*for (x in Tabu){
            if (System.currentTimeMillis() > endTime){
                break
            }
            if (localSearchKnapsack.calculateFitness(x) > localSearchKnapsack.calculateFitness(S)){
                neighborhood = add(neighborhood,x)
            }
        }*/
        return neighborhood
    }

    fun tabuSearch() : IntArray{
        var S_0 = localSearchKnapsack.generateGreedySolution()
        var S_p = S_0.copyOf()
        var VA = localSearchKnapsack.neighbors(S_0)       // veccindad aceptable que no es tabu
        var Tabu = mutableListOf<IntArray>()    //movimientos tabu
        endTime = System.currentTimeMillis() + TIME_LIMIT_MS
        for (i in 0 until MAX_ITERATIONS) {
            if (System.currentTimeMillis() > endTime){
                break
            }
            S_p = best_VA(VA,S_p)
            VA = updateTabu(VA,S_p,Tabu)
            if (localSearchKnapsack.calculateFitness(S_p)>localSearchKnapsack.calculateFitness(S_0)){
                S_0 = S_p.copyOf()
            }
        }
        return S_p

    }
}