data class TestResult (
    val testInstance: String,
    val algorithmVariant: String,
    val correlation : String,
    val size: Int,
    val capacity: Int,
    val expected: Int,
    val obtained: Int,
    val error: Int,
    val executionTime: Long
) {
    companion object {
        fun getHeader(): String {
            return "Instance, Algorithm, Correlation, Size, Capacity, Expected, Obtained, Error, Time (ms)"
        }
    }

    override fun toString(): String {
        return "$testInstance, $algorithmVariant, $correlation, $size, $capacity, $expected, $obtained, $error, $executionTime"
    }
}