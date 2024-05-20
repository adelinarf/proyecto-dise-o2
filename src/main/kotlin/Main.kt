package main.kotlin

import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.max

suspend fun main() {
    // val w = intArrayOf(23, 31, 29, 44, 53, 38, 63, 85, 89, 82)
    // val p = intArrayOf(92, 57, 49, 68, 60, 43, 67, 84, 87, 72)
    // val c = 165
    // val p = intArrayOf(6, 10, 12)
    // val w = intArrayOf(1, 2, 3)
    // val c = 5
    // val n = w.size
    // val o = intArrayOf(1, 1, 1, 1, 0, 1, 0, 0, 0, 0)

    // println(knapSack2(c, w, p, n))
    // println(o.zip(p).sumOf { (a, b) -> a * b })
    // println()
    // Benchmark().load()

    // var res1 = knapSack2(c, w, p, n)
    // println(knapSack2(c, w, p, n))
    // // println(o.zip(p).sumOf { (a, b) -> a * b })
    // println()
    // var res2 = knapsack_heuristics(c, w, p, n,"QBH01")
    // println(knapsack_heuristics(c, w, p, n,"QBH01"))
    // var res3 = knapsack_heuristics(c,w,p,n,"QBH02")
    // var res4 = knapsack_heuristics(c,w,p,n,"QBHH")
    // var res5 = knapsack_heuristics(c,w,p,n,"max")

    // println()

    // println(res1)
    // println(res2)
    // println(res3)
    // println(res4)
    // println(res5)

    // //ejemplo como el paper para verificar los valores
    // var p4 = heur(c, w, p, n)
    // println(p4)

    // var p5 = knapsack_heuristics(c,w,p,n,"default")
    // println(p5)
    // var p6 = knapsack_heuristics(c,w,p,n,"profit/weight")
    // println(p6)
    // var p7 = knapsack_heuristics(c,w,p,n,"minweight")
    // println(p7)

    println(knapSack3(50, intArrayOf(10, 20, 30), intArrayOf(60, 100, 120), 3))
}

fun knapSack(c: Int, w: IntArray, p: IntArray, n: Int): Int {
    if (n == 0 || c == 0) return 0

    return if (w[n - 1] > c) knapSack(c, w, p, n - 1)
    else max(p[n - 1] + knapSack(c - w[n - 1], w, p, n - 1), knapSack(c, w, p, n - 1))
}

suspend fun knapSack2(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long = 10000): Int {
    var result: Int = -1

    val m = Array(n + 1) { IntArray(c + 1) }
    for (j in 0..c) m[0][j] = 0
    for (i in 1..n) m[i][0] = 0

    for (i in 1..n) {
        for (j in 1..c) {
            m[i][j] = if (w[i - 1] > j) m[i - 1][j] else max(m[i - 1][j], m[i - 1][j - w[i - 1]] + p[i - 1])
        }
    }

    result = m[n][c]

    return result
}

suspend fun knapSack3(c: Int, w: IntArray, p: IntArray, n: Int, timeout: Long = 10000): Int {
    var result: Int = -1

    println("c: $c")
    println("w: ${w.toList()}")
    println("p: ${p.toList()}")
    println("n: $n")

    //withTimeoutOrNull(timeout) {
    val dp = IntArray(c + 1)

    for (i in 1 until n + 1) {
        for (j in c downTo 0) {
            if (w[i - 1] <= j) dp[j] = max(dp[j], dp[j - w[i - 1]] + p[i - 1]
            )
        }
    }

    result = dp[c]
    //}

    return result
}
