

fun main() {
    Benchmark().test()
    println("Tests done!")
    println("Active threads: ${Thread.activeCount()}")
}