import java.util.*

data class TestCase(
    val name: String,
    val n: Int,
    val c: Int,
    val p: List<Int>,
    val w: List<Int>,
    // val s: List<Int>,
    val o: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestCase

        if (name != other.name) return false
        if (n != other.n) return false
        if (c != other.c) return false
        if (p != other.p) return false
        if (w != other.w) return false
        // if (s != other.s) return false
        if (o != other.o) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + n.hashCode()
        result = 31 * result + c.hashCode()
        result = 31 * result + p.hashCode()
        result = 31 * result + w.hashCode()
        // result = 31 * result + s.hashCode()
        result = 31 * result + o.hashCode()
        return result
    }

    override fun toString(): String {
        return "Test: $name\n Instances: $n\n Capacity: $c\n Profits: $p\n Weight: $w\n ------------------------\n"
    }
}
