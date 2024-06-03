package main.kotlin

import main.kotlin.LocalSearchKnapsack
import kotlin.math.max

fun criterio_aceptacion(best_Sol: IntArray, next_Best : IntArray,memoria: MutableList<IntArray>, localSearch: LocalSearchKnapsack) : IntArray {
    val a = localSearch.calculateFitness(best_Sol)
    val b = localSearch.calculateFitness(next_Best)
    if (a > b){
        if (!inMutableList(best_Sol,memoria)){
            memoria.add(best_Sol)
            return best_Sol
        }
        return best_Sol
    }
    if (!inMutableList(next_Best,memoria)){
        memoria.add(next_Best)
        return next_Best
    }
    return best_Sol
}

fun inMutableList(e : IntArray, m: MutableList<IntArray>) : Boolean{
    var contiene = false
    for (x in m){
        val z = x contentEquals e
        contiene = contiene || z
    }
    return contiene
}

fun perturbacion(originalS:IntArray, memoria: MutableList<IntArray>, originalW: IntArray,capacity : Int,localSearch: LocalSearchKnapsack) : IntArray{
    //sacar al mas grande y agregarle los chicos que sumen su cantidad y que no hayan sido tomados
    val S = originalS.copyOf()
    val w = originalW.copyOf()
    var maximum=0
    while (maximum != -1) {
        maximum = w.maxOrNull() ?: -1
        if (maximum==-1){
            break
        }
        var index = w.indexOf(maximum)
        if (S[index] == 1) {
            var sumados = 0
            var conseguidos: MutableList<Int> = mutableListOf()
            for (x in 0..S.size-1) {
                if (S[x] == 0 && (w[x] + sumados) <= w[index] && (w[x] +sumados) <= capacity && w[x]!=-1) {
                    sumados += w[x]
                    conseguidos.add(x)
                }
            }
            if (conseguidos.size>=1){
                S[index] = 0
                for (y in 0..conseguidos.size-1) {
                    S[conseguidos[y]] = 1
                }
            }
            w[index]=-1
        }
        else{
            w[index] = -1
        }
    }
    if (localSearch.calculateFitness(S) > localSearch.calculateFitness(originalS)){
        if (!inMutableList(S,memoria)){
            memoria.add(S)
            return S
        }
        return originalS
    }
    return originalS
}
fun criterio_terminacion(sol : IntArray) : Boolean {
    return false;
}
fun iterativeLocalSearch() : IntArray{
    val capacity = 10
    val w = intArrayOf(2, 3, 5, 7)
    val p = intArrayOf(10, 15, 20, 25)
    val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, w.size)
    val S_0 = localSearchKnapsack.generateGreedySolution()
    var S_p = localSearchKnapsack.localSearch2(greedy=S_0)
    println(S_p.toList())
    val memoria : MutableList<IntArray> = mutableListOf()
    var counter = 0
    while (counter<100){//(criterio_terminacion(S_p) == false){
        val Sp = perturbacion(S_p,memoria,w,capacity,localSearchKnapsack);
        val Spp = localSearchKnapsack.localSearch2(greedy=Sp);
        S_p = criterio_aceptacion(S_p,Spp,memoria,localSearchKnapsack)
        counter+=1
    }
    return S_p;
}
fun main(){
    val a = iterativeLocalSearch()
    println(a.toList())
}