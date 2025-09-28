package me.emaryllis.data

data class Chunk(val minValue: Int, val maxValue: Int, val values: List<Int>) {
    operator fun contains(x: Int): Boolean = x in minValue..maxValue
}
