package me.emaryllis.chunk

import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.data.Stack
import me.emaryllis.utils.Utils.sorted

class SmallSort {
	fun smallSort(stack: Stack): List<Move> {
		if (stack.a == stack.a.sorted()) return emptyList()
		return when (stack.a.size) {
			0, 1 -> emptyList()
			2, 3 -> sortThree(stack.a)
			4, 5 -> sortFourFive(stack)
			else -> error("SmallSort can only handle up to 5 elements, but got ${stack.a.size}")
		}
	}

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

	/**
	 * Sorts a stack of four or five elements by pushing the smallest one or two to stack B.
	 * Computes 1-2 moves more than A*, but at a reduced time complexity.
	 */
	fun sortFourFive(stack: Stack): List<Move> {
		val numToPush = if (stack.a.size == 5) 2 else 1

		repeat(numToPush) {
			// Find index of minimum in A
			val minIndex = (0 until stack.a.size).minByOrNull { stack.a[it] } ?: 0
			if (minIndex <= stack.a.size / 2) {
				repeat(minIndex) {
					stack.apply(Move.RA)
				}
			} else {
				repeat(stack.a.size - minIndex) {
					stack.apply(Move.RRA)
				}
			}
			stack.apply(Move.PB)
		}
		sortThree(stack.a).forEach(stack::apply)
		repeat(numToPush) {
			stack.apply(Move.PA)
		}
		return stack.moves
	}
}