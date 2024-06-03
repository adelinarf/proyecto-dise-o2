package main.kotlin
import main.kotlin.LocalSearchKnapsack
import kotlin.math.ceil
import kotlin.math.exp
/*fun criterio_enfriamiento(S:IntArray){

}
fun updateTemp(T){

}*/
fun vecino(vecinos : Sequence<IntArray>):IntArray{
    for (x in vecinos){
        return x
    }
    return intArrayOf()
}
fun P(x : IntArray,y : IntArray,T : Float) : Double{
    val thirdArray=x.zip(y).map { (it.second - it.first) }
    if (T.toInt()!=0){
        val fourth = thirdArray.map{ (it/T).toDouble() }
        return fourth.max()
    }
    return thirdArray.max().toDouble()

    /*val A = (y-x)/T
    if (x < y){
        return 1.0
    }
    return exp(-(y-x)/T)*/
}

fun SimulatedAnnealing() : IntArray {
    val capacity = 10
    val w = intArrayOf(2, 3, 5, 7)
    val p = intArrayOf(10, 15, 20, 25)
    val localSearchKnapsack = LocalSearchKnapsack(capacity, w, p, w.size)
    var S_0 = localSearchKnapsack.generateGreedySolution()
    var S_p : IntArray = S_0.copyOf()
    var T = 0
    var kmax=10
    var counter=0
    while (counter<100){ //criterio_terminacion(S_p)==false
        var vecinos = localSearchKnapsack.neighbors(S_0)
        for (x in 1..kmax){
            var T = (x/kmax).toFloat()
            var S_new : IntArray = vecino(vecinos)
            if (P(S_p,S_new,T) >= (0..1).random()){
                S_p = S_new.copyOf()
            }
        }
        counter+=1


        /*while(criterio_enfriamiento(S_p)){
            for (S__p in vecinos){
                var probabilidad = P[S__p]
                S_p = S__p.copyOf()
                if (localSearchKnapsack.calculateFitness(S__p)>localSearchKnapsack.calculateFitness(S_0)){
                    S_0=S__p.copyOf()
                }
            }
        }*/
        //updateTemp(T)
    }
    println(S_p.toList())
    return S_p
}
fun main(){
    SimulatedAnnealing()
}