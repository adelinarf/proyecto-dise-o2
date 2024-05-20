data class TestResult (
    val testInstance: String,
    val algorithmVariant: String,
    val expected: Int,
    val obtained: Int,
    val error: Int,
    val executionTime: Long
) {
    companion object {
        fun getHeader(): String {
            return "Instance, Algorithm, Expected, Obtained, Error, Time"
        }
    }

    override fun toString(): String {
        return "$testInstance, $algorithmVariant, $expected, $obtained, $error, $executionTime"
    }
}