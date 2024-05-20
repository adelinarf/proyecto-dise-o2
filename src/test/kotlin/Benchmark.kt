import kotlinx.coroutines.*
import main.kotlin.Algorithms
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.measureNanoTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class Benchmark {

    // private val sources : List<String> = listOf("large_scale", "low-dimensional")
    private val sources : List<String> = listOf("heavy")
    private val classLoader = Thread.currentThread().contextClassLoader

    /*private fun load(): List<TestCase> {
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
    }*/

    private fun loadAlt() : List<TestCase> {
        val instances : MutableList<TestCase> = ArrayList()

        for (source in sources) {
            // Capture the state file
            val folder = File(classLoader.getResource("benchmark_alt/$source")?.file ?: "")

            folder.listFiles()?.forEach { file ->
                if (file.name.contains("skip") || file.name.contains("README")) return@forEach
                //if (file.name != "knapPI_16_10000_1000.csv") return@forEach


                var p = Vector<Int>()
                var w = Vector<Int>()
                var s = Vector<Int>()

                val lines = file.readLines()
                var i = 0
                while (i < lines.size) {
                    if (lines[i].isBlank() || lines[i].startsWith("-")) {
                        i++
                        p = Vector<Int>()
                        w = Vector<Int>()
                        s = Vector<Int>()
                        continue
                    }

                    val name = lines[i++]
                    val n = lines[i++].split(" ")[1].toInt() - 1
                    val c = lines[i++].split(" ")[1].toInt()
                    val o = lines[i++].split(" ")[1].toInt()

                    val limit = i++
                    while (i < lines.size && lines[i].isNotEmpty() && i <= limit + n) {
                        val parts = lines[i++].split(",")
                        p.add(parts[1].toInt())
                        w.add(parts[2].toInt())
                        s.add(parts[3].toInt())
                    }
                    i++

                    // Create the test case
                    val testCase = TestCase(name, n, c, p.take(n), w.take(n), o)
                    instances.add(testCase)
                }
            }
        }

        return instances
    }

    fun test(): Unit = runBlocking(Dispatchers.Default) {
        val instances = loadAlt()
        println(instances.size)
        val results: MutableList<TestResult> = ArrayList()

        val jobs = instances.flatMap { instance ->
            Algorithms.values().map { algorithm ->
                launch {
                    println("Testing ${instance.name} with ${algorithm.name}...")

                    var r: Int
                    var time: Long

                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit<Int> {
                        algorithm.solve(instance.c, instance.w.toIntArray(), instance.p.toIntArray(), instance.n)
                    }
                    
                        try {
                            time = measureNanoTime {
                                r = future.get(60, TimeUnit.SECONDS) // Time limit for each test
                            }
                            println("Finished test for ${instance.name} with ${algorithm.name}.")
                        } catch (e: TimeoutException) {
                            future.cancel(true)
                            r = -1
                            time = -1000
                        } finally {
                            executor.shutdownNow()
                        }


                    val testResult = TestResult(instance.name, algorithm.name, instance.o, r, instance.o - r, time/1000)
                    synchronized(results) {
                        results.add(testResult)
                    }
                }
            }
        }

        // Wait for all coroutines to finish
        println("Starting join for all jobs...")
        jobs.forEach { it.join() }
        println("Finished join for all jobs.")

        // Export the results
        val path = Paths.get("src/test/resources/output.csv")
        val lines = results.map { it.toString() }.toMutableList()
        lines.add(0, TestResult.getHeader())
        Files.write(path, lines)
        withContext(Dispatchers.IO) { Files.write(path, lines) }
        println("Finished writing all the test results.")

    }
}