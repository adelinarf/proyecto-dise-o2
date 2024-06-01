data class TestCase(
    val name: String,
    val n: Int,
    val c: Int,
    val p: List<Int>,
    val w: List<Int>,
    val z: Int,
    val s: List<Int>,
    val t: String
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
        if (z != other.z) return false
        if (s != other.s) return false
        if (t != other.t) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + n.hashCode()
        result = 31 * result + c.hashCode()
        result = 31 * result + p.hashCode()
        result = 31 * result + w.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + s.hashCode()
        result = 31 * result + t.hashCode()
        return result
    }

    override fun toString(): String {
        return "$name\n N: $n\n C: $c\n P: $p\n W: $w\n Z: $z\n T: $t\n ------------------------\n"
    }
}
