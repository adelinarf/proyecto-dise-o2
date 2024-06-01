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

    private val classLoader = Thread.currentThread().contextClassLoader

    fun load() : List<TestCase> {
        // Selection of instances
        val sources : List<String> = listOf("hard")
        val sizes : List<Int> = listOf(100, 500, 1000, 5000, 10000)
        val coefRanges : List<Int> = listOf(1000)
        val types : Map<Int, String> = mapOf(
            11 to "uncorrelated",
            12 to "weakly_correlated",
            13 to "strongly_correlated",
            14 to "mstr",
            15 to "pceil",
            16 to "circle"
        )

        // Reading process
        val instances : MutableList<TestCase> = ArrayList()
        for (source in sources) {
            val folder = File(classLoader.getResource("pisinger/$source")?.file ?: "")

            folder.listFiles()?.forEach { file ->
                // Check if file is from the selected sizes and coefRanges
                val fileName = file.name.split(".")[0].split("_")
                val type = fileName[1].toInt()
                val size = fileName[2].toInt()
                val coefRange = fileName[3].toInt()
                if (!sizes.contains(size) || !coefRanges.contains(coefRange)) return@forEach;

                // Read the first instance
                val p = Vector<Int>()
                val w = Vector<Int>()
                val s = Vector<Int>()
                var i = 0
                val lines = file.readLines()
                while (i < lines.size) {
                    if (lines[i].isBlank() || lines[i].startsWith("-")) break

                    val name = lines[i++]
                    val n = lines[i++].split(" ")[1].toInt() - 1
                    val c = lines[i++].split(" ")[1].toInt()
                    val z = lines[i++].split(" ")[1].toInt()

                    val limit = i++
                    while (i < lines.size && lines[i].isNotEmpty() && i <= limit + n) {
                        val parts = lines[i++].split(",")
                        p.add(parts[1].toInt())
                        w.add(parts[2].toInt())
                        s.add(parts[3].toInt())
                    }
                    i++

                    // Create the test case
                    val testCase = TestCase(name, n, c, p.take(n), w.take(n), z, s, types[type] ?: "unknown")
                    instances.add(testCase)
                }
            }
        }

        //instances.forEach(::println)
        println(instances.size)
        return instances
    }

    fun test(): Unit = runBlocking(Dispatchers.Default) {
        val instances = load()
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


                    val testResult = TestResult(
                        instance.name,
                        algorithm.name,
                        instance.n,
                        instance.c,
                        instance.z,
                        r,
                        instance.z - r,
                        time/1000)
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