import main.kotlin.AntsColony
import kotlin.system.exitProcess


fun main() {
    Benchmark().test(true)
    println("Tests done!")
    println("Active threads: ${Thread.activeCount()}")
    exitProcess(0)

    //val p = intArrayOf(60, 100, 120)
    //val w = intArrayOf(10, 20, 30)
    //val c = 50
//
    //val ants = AntsColony(p.size, c, w, p, 10)
    //val sol = ants.solve()
    //println(sol.zip(p).sumOf { (a, b) -> a * b })
//
    //exitProcess(0)
}