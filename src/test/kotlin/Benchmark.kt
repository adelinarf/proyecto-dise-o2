import kotlinx.coroutines.*
import main.kotlin.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.system.measureTimeMillis

class Benchmark {
    // Includes slack time for calculating result data
    private val TEST_LIMIT_MS: Long  get() = TIME_LIMIT_MS + 300_000L
    private val algorithms = mutableListOf<String>()
    init {
        // Set global testing parameters
        TIME_LIMIT_MS = 900_000
        MAX_ITERATIONS = 100_000
        MAX_ITER_WITHOUT_IMPROVE = 10
        SHORT_MAX_ITERATIONS = 1000
        algorithms.addAll(
            mutableListOf(
                //  "KNAPSACK_DP",
                //  "KNAPSACK_HEURISTICS",
                //  "KNAPSACK_LOCAL_SEARCH_SWAP",
                //  "KNAPSACK_LOCAL_SEARCH_FLIP",
                //  "KNAPSACK_ITERATIVE_LOCAL_SEARCH",
                //  "KNAPSACK_TABU_SEARCH",
                //  "KNAPSACK_SIMULATED_ANNEALING",
                //  "KNAPSACK_GENETIC",
                //  "KNAPSACK_GRASP",
                //  "KNAPSACK_MEMETIC",
                //   "KNAPSACK_SCATTER_SEARCH",
                //  "KNAPSACK_ANTS",
                "KNAPSACK_BRO"
            )
        )
    }

    private val classLoader = Thread.currentThread().contextClassLoader

    fun load() : List<TestCase> {
        // Selection of instances
        val sources: List<String> = listOf("small-coef")
        val sizes: List<Int> = listOf(/*100, 500, 1000, 5000, */10000)
        val coefRanges : List<Int> = listOf(10000)
        val types : Map<Int, String> = mapOf(
            1 to "uncorrelated",
            2 to "weakly correlated",
            3 to "strongly correlated",
            4 to "inverse strongly correlated",
            5 to "almost strongly correlated",
            6 to "subset sum",
            9 to "similar weights",

            // 11 to "uncorrelated",
            // 12 to "weakly_correlated",
            // 13 to "strongly_correlated",
            // 14 to "mstr",
            // 15 to "pceil",
            // 16 to "circle"
        )

        // Reading process
        val instances: MutableList<TestCase> = ArrayList()
        for (source in sources) {
            val folder = Paths.get(classLoader.getResource("pisinger/$source")?.toURI())
            val files = Files.list(folder).map { it.toFile() }
            val fileInstances: MutableList<TestCase> = ArrayList()
            files.forEach { file ->
                // Check if file is from the selected sizes and coefRanges
                val fileName = file.name.split(".")[0].split("_")
                val type = fileName[1].toInt()
                val size = fileName[2].toInt()
                val coefRange = fileName[3].toInt()
                if (!sizes.contains(size) || !coefRanges.contains(coefRange) || !types.contains(type)) return@forEach;

                // Read the first instance
                var p = Vector<Int>()
                var w = Vector<Int>()
                var s = Vector<Int>()
                var i = 0
                val lines = file.readLines()
                while (i < lines.size) {
                    //if (lines[i].isBlank() || lines[i].startsWith("-")) break
                    if (lines[i].isBlank() || lines[i].startsWith("-")) {
                        i++
                        p = Vector<Int>()
                        w = Vector<Int>()
                        s = Vector<Int>()
                        continue
                    }

                    val name = lines[i++]
                    val n = lines[i++].split(" ")[1].toInt()
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
                    fileInstances.add(testCase)
                }
                instances.add(fileInstances[fileInstances.size - 1])
            }
        }

        // instances.forEach(::println)
        // println(instances.size)
        return instances
    }

    fun test(): Unit = runBlocking(Dispatchers.Default) {
        val instances = load()
        println("Loaded ${instances.size} instances.")
        val results: MutableList<TestResult> = ArrayList()

        val jobs = instances.flatMap { instance ->
            Algorithms.values().filter{ algorithm -> isActive(algorithm) }.map { algorithm ->
                launch {
                    println("Testing ${instance.name} with ${algorithm.name}...")

                    var r: Int
                    var time: Long

                    val executor = Executors.newSingleThreadExecutor()
                    val future = executor.submit<Int> {
                        algorithm.solve(instance.c, instance.w.toIntArray(), instance.p.toIntArray(), instance.n)
                    }
                    
                        try {
                            time = measureTimeMillis {
                                r = future.get(TEST_LIMIT_MS, TimeUnit.MILLISECONDS) // Time limit for each test
                            }
                            println("Finished test for ${instance.name} with ${algorithm.name}.")
                        } catch (e: TimeoutException) {
                            future.cancel(true)
                            r = -1
                            time = Long.MAX_VALUE
                        } finally {
                            executor.shutdownNow()
                        }


                    val testResult = TestResult(
                        instance.name,
                        algorithm.name,
                        instance.t,
                        instance.n,
                        instance.c,
                        instance.z,
                        r,
                        instance.z - r,
                        time)
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

    fun isActive(algorithm: Algorithms): Boolean {
        return algorithms.contains(algorithm.name)
    }
}