package main.kotlin
import main.kotlin.LocalSearchKnapsack


fun best_VA(neighborhood : Sequence<IntArray>,S : IntArray, l : LocalSearchKnapsack) : IntArray {
    //val neighborhood = l.neighbors(S)
    return neighborhood.maxByOrNull { l.calculateFitness(it) }  ?: intArrayOf()
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

fun updateTaboo(neighborhood : Sequence<IntArray>,w:IntArray,capacity:Int,S:IntArray,Tabu: MutableList<IntArray>,l:LocalSearchKnapsack) : Sequence<IntArray>{
    var neighborhood = l.neighbors(S)
    var counter = 0
    for (y in neighborhood){
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
    }
    //Criterio de aceptacion, si esta en Tabu pero logra un mejor resultado, se agrega a la vecindad
    for (x in Tabu){
        if (l.calculateFitness(x) > l.calculateFitness(S)){
            neighborhood = add(neighborhood,x)
        }
    }
    return neighborhood
}

fun tabooSearch() : IntArray{
    val capacity = 10
    val w = intArrayOf(2, 3, 5, 7)
    val p = intArrayOf(10, 15, 20, 25)
    val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, w.size)
    var S_0 = localSearchKnapsack.generateGreedySolution()
    var S_p = S_0.copyOf()
    var VA = localSearchKnapsack.neighbors(S_0)       // veccindad aceptable que no es tabu
    var Tabu = mutableListOf<IntArray>()    //movimientos tabu
    var lastFitness=0
    var count=0
    var currentFitness = localSearchKnapsack.calculateFitness(S_p)
    while (count<100){ //(criterio_terminacion(S_p)==false){
        S_p = best_VA(VA,S_p,localSearchKnapsack)
        VA = updateTaboo(VA,w,capacity,S_p,Tabu,localSearchKnapsack)
        if (localSearchKnapsack.calculateFitness(S_p)>localSearchKnapsack.calculateFitness(S_0)){
            S_0 = S_p.copyOf()
        }
        count+=1
    }
    println(S_p.toList())
    return S_p

}
fun main(){
    tabooSearch()
}