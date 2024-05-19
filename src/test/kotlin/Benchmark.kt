import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import main.kotlin.Algorithms
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.measureTimeMillis

class Benchmark {

    // private val sources : List<String> = listOf("large_scale", "low-dimensional")
    private val sources : List<String> = listOf("low-dimensional")
    private val classLoader = Thread.currentThread().contextClassLoader

    private fun load(): List<TestCase> {
        val instances : MutableList<TestCase> = ArrayList()

        for (source in sources) {
            // Capture the state file
            val folder = File(classLoader.getResource("benchmark/$source")?.file ?: "")

            folder.listFiles()?.forEach { file ->
                if (file.name.contains("skip")) return@forEach

                val p = Vector<Int>()
                val w = Vector<Int>()

                file.forEachLine { line ->
                    val (value1, value2) = line.split(" ").map { it.toInt() }
                    p.add(value1)
                    w.add(value2)
                }

                // Capture the optimal result file
                val optimumFileName = "benchmark/$source-optimum/${file.name}"
                val optimumFile = File(classLoader.getResource(optimumFileName)?.file ?: "")
                val o = optimumFile.readLines().first().toInt()

                // Create the test case
                val n = p.removeAt(0).toInt()
                val c = w.removeAt(0).toInt()
                val testCase = TestCase(file.name, n, c, p.take(n), w.take(n), o)
                instances.add(testCase)
            }
        }

        return instances
    }

    fun test(): Unit = runBlocking(Dispatchers.Default) {
        val instances = load()
        val results : MutableList<TestResult> = ArrayList()

        val jobs = instances.flatMap { instance ->
            Algorithms.entries.map { algorithm ->
                launch {
                    println("Testing ${instance.name} with ${algorithm.name}...")

                    var r: Int
                    val time = measureTimeMillis {
                        r = algorithm.solve(instance.c, instance.w.toIntArray(), instance.p.toIntArray(), instance.n)
                    }
                    val testResult = TestResult(instance.name, algorithm.name, instance.o, r, instance.o - r, time)
                    results.add(testResult)
                }
            }
        }

        // Wait for all coroutines to finish
        jobs.forEach { it.join() }

        // Export the results
        val path = Paths.get("src/test/resources/output.csv")
        val lines = results.map { it.toString() }.toMutableList()
        lines.add(0, TestResult.getHeader())
        withContext(Dispatchers.IO) { Files.write(path, lines) }
    }
}