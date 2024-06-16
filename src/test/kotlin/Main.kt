import kotlin.system.exitProcess


suspend fun main() {
    // Benchmark().load()
    Benchmark().test()
    println("Tests done!")
    println("Active threads: ${Thread.activeCount()}")
    exitProcess(0)
}