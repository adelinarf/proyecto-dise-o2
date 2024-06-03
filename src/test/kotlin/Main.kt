

suspend fun main() {
    // Benchmark().load()
    Benchmark().test()
    println("Tests done!")
    println("Active threads: ${Thread.activeCount()}")
}