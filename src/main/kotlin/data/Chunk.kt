package me.emaryllis.data

data class Chunk(val minValue: Int, val maxValue: Int, val values: List<Int>) {
	fun clone() = Chunk(minValue, maxValue, values.toList())
}
