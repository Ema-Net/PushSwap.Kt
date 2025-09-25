package me.emaryllis.data

data class Chunk(val minValue: Int, val maxValue: Int, val values: List<Int>) {
	fun clone() = Chunk(minValue, maxValue, values.toList())
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Chunk) return false
		return hashCode() == other.hashCode()
	}

	override fun hashCode(): Int {
		var result = minValue
		listOf(maxValue, *values.toTypedArray()).forEach { result = 31 * result + it }
		return result
	}
}
