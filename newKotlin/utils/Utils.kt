package me.emaryllis.utils

import me.emaryllis.data.CircularBuffer

object Utils {
	fun <T> List<T>.hasDuplicates(): Boolean {
		val seen = mutableSetOf<T>()
		for (item in this) {
			if (!seen.add(item)) return true
		}
		return false
	}

	fun CircularBuffer.sorted() = this.clone().apply { this.buffer = this.buffer.sortedArray() }
}