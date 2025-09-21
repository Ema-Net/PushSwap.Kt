package me.emaryllis.chunk

import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.utils.Utils.sorted

class SmallSort {
	fun sortThree(buffer: CircularBuffer): List<Move> {
		if (buffer == buffer.sorted() || buffer.size < 2) return emptyList()
		if (buffer.size == 2) {
			if (buffer[0] > buffer[1]) return listOf(Move.SA)
			return emptyList()
		}

		val a = buffer[0]
		val b = buffer[1]
		val c = buffer[2]
		return when {
			b in (a + 1)..<c -> emptyList()
			a in (b + 1)..<c && b < c -> listOf(Move.SA)
			b in (c + 1)..<a -> listOf(Move.SA, Move.RRA)
			a > b && b < c && c < a -> listOf(Move.RA)
			a < b && b > c && c > a -> listOf(Move.SA, Move.RA)
			a in (c + 1)..<b && b > c -> listOf(Move.RRA)
			else -> error("Invalid state: a=$a, b=$b, c=$c")
		}
	}
}