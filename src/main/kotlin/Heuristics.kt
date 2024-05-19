package main.kotlin

import kotlin.math.*

fun Q1W(w: IntArray, p: IntArray) : List<Pair<Int, Int>> {
    //25% first lighest
    var zipped = (w zip p)
    var ordered = zipped.sortedByDescending { it.first }
    return ordered.take(ceil(ordered.size*0.25).toInt())
}

fun Q4W(w: IntArray, p: IntArray) : List<Pair<Int, Int>> {
    //25% heaviest
    var zipped = (w zip p)
    var ordered= zipped.sortedByDescending { it.first }
    return ordered.takeLast(ceil(ordered.size*0.25).toInt())
}

fun IQRW(w: IntArray, p: IntArray) : List<Pair<Int, Int>> {
    //50% middle
    var zipped = (w zip p)
    var q1 = Q1W(w,p)
    var q4 = Q4W(w,p)
    var lista : MutableList<Any> = mutableListOf()
    lista.addAll(q1)
    lista.addAll(q4)
    val referenceIds = lista.distinctBy { it }.toSet()
    return zipped.filterNot { it in referenceIds }
}

fun QBH01(w: IntArray, p: IntArray) : Int {
    var q1 = Q1W(w,p)
    var lightest = q1.sortedByDescending { it.second }
    var iqrw = IQRW(w,p)
    var iqrw_sorted = iqrw.sortedBy { it.second/it.first }
    var maximum = max(lightest[0].second,iqrw_sorted[0].second)
    return maximum
}


fun mean(p:IntArray):Int{
    return p.sum()/p.size
}
fun sd(p:IntArray): Double {
    var sizew = p.size
    var means =  MutableList(sizew){ 0 }
    for (i in 0..sizew-1) {
        means[i] = (p[i]-mean(p))*(p[i]-mean(p))
    }
    return sqrt((1/sizew*(means.sum())).toDouble())
}

fun QBH02(w: IntArray, p: IntArray) : Int {
    var iqrw = IQRW(w,p)
    var filtering = iqrw.filter { it.second > (mean(p)+sd(p)) }
    var sorting = iqrw.sortedByDescending { it.second }
    return sorting[0].second
}

fun maximum_profit(m : Array<IntArray>,p : IntArray,i:Int,j:Int,w:IntArray) : Int {
    return max(m[i - 1][j], m[i - 1][j - w[i - 1]] + p[i - 1])
}

fun default(p : IntArray,i:Int,j:Int,w:IntArray): Int {
    if (w[i-1]<j){
        return p[i-1]
    }
    else{
        return 0
    }
}

fun QBHH(w:IntArray, p:IntArray,m : Array<IntArray>,i:Int,j:Int) : Int {
    try{
        return QBH01(w,p)
    }catch(e : Exception){
        try{
            return QBH02(w,p)
        }
        catch (e : Exception){
            return maximum_profit(m,p,i,j,w)
        }
    }
}
/*
Max profit per weight. Packs the item with the highest ratio of profit over weight.*/
fun max_profit_per_weight(w:IntArray, p:IntArray) : Int {
    var s = (w zip p).sortedByDescending { it.second/it.first }
    return s[0].second
}

fun min_weight(m : Array<IntArray>,p : IntArray,i:Int,j:Int,w:IntArray) : Int{
    var s = (w zip p).sortedBy { it.first }
    return s[0].second
}

fun heuristic(w:IntArray, p:IntArray,m : Array<IntArray>,i:Int,j:Int,h:String) : Int{
    if (h == "max"){
        return maximum_profit(m,p,i,j,w)
    }
    else if (h == "QBHH"){
        return QBHH(w, p,m ,i,j)
    }
    else if (h == "QBH02") {
        return QBH02(w,p)
    }
    else if (h == "QBH01"){
        return QBH01(w,p)
    }
    else if (h == "default"){
        return default(p,i,j,w)
    }
    else if (h == "profit/weight"){
        return max_profit_per_weight(w,p)
    }
    else if (h == "minweight"){
        return min_weight(m,p,i,j,w)
    }
    return -1
}
// h puede ser "max" , "QBHH" , "QBH02" , "QBH01", "default", "profit/weight", "minweight"
fun knapsack_heuristics(c: Int, w: IntArray, p: IntArray, n: Int,h:String): Int {
    val m = Array(n + 1) { IntArray(c + 1) }
    for (j in 0..c) m[0][j] = 0
    for (i in 1..n) m[i][0] = 0

    var rep : String = m.map { it.toList().toString() + "\n"}.toString()
    println(rep.replace("[", "").replace("]", "").replace(",", ""))

    for (i in 1..n) {
        for (j in 1..c) {
            m[i][j] = if (w[i - 1] > j) m[i - 1][j] else heuristic(w, p, m, i, j, h)
            //max(m[i - 1][j], m[i - 1][j-w[i-1]] + QBH01(w,p)) NO DA EL RESULTADO ESPERADO

            rep = m.map { it.toList().toString() + "\n"}.toString()
            //println("\ni = $i j = $j")
            //println(rep.replace("[", "").replace("]", "").replace(",", ""))
        }
    }
    return m[n][c]
}

fun heur(c: Int, w1: IntArray, p1: IntArray, n: Int) : Int {
    var w : MutableList<Int> = mutableListOf()
    var p : MutableList<Int> = mutableListOf()
    for (j in 0..w1.size-1){
        w.add(w1[j])
    }
    for (j in 0 .. p1.size-1){
        p.add(p1[j])
    }

    var weight = 0
    while (weight < c && w.size >0){
        for (j in 0..w.size-1){
            if ((w[j]+weight) > c){
                w.remove(w[j])
                p.remove(p[j])
            }
        }
        var h1 = 0
        try{
            h1 = QBH01(w.toIntArray(),p.toIntArray())
            weight +=h1
            println("QBH01")
        }catch (e : Exception){
            try{
                h1 = QBH02(w.toIntArray(),p.toIntArray())
                weight+=h1
                println("QBH02")
            }catch (e : Exception){
                h1 = (p.sortedByDescending { it })[0]
                weight += h1
                println("MAX")
            }
        }
    }
    return weight
}