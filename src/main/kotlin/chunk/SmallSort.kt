package me.emaryllis.chunk

import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.data.Stack
import me.emaryllis.utils.Utils.sorted

/**
 * SmallSort provides optimal or near-optimal sorting for stacks of size 5 or less.
 * Purpose: Efficiently sorts very small stacks using hardcoded strategies, minimizing moves.
 *
 * Time & Space Complexity: See [smallSort]
 */
class SmallSort {
	/**
	 * Sorts a stack of up to 5 elements using minimal [Move].
	 * - If already sorted, returns an empty list.
	 * - For 2 or 3 elements, uses sortThree.
	 * - For 4 or 5 elements, uses sortFourFive.
	 *
	 * Time complexity: O(1) (constant number of cases).
	 * Space complexity: O(k), k = number of moves returned.
	 *
	 * @return List of moves to sort the stack.
	 */
	fun smallSort(stack: Stack): List<Move> {
		if (stack.a == stack.a.sorted()) return emptyList()
		return when (stack.a.size) {
			0, 1 -> emptyList()
			2, 3 -> sortThree(stack.a)
			4, 5 -> sortFourFive(stack)
			else -> error("SmallSort can only handle up to 5 elements, but got ${stack.a.size}")
		}
	}

	/**
	 * Sorts a buffer of 2 or 3 elements using hardcoded [Move] sequences.
	 * Purpose: Returns the minimal sequence of moves to sort the buffer.
	 * - For 2 elements, swaps if needed.
	 * - For 3 elements, uses case analysis for all possible orderings.
	 *
	 * Time complexity: O(1) (fixed number of cases).
	 *
	 * Space complexity: O(k), k = number of moves
	 *
	 * @return List of moves to sort the buffer. (0-2 moves)
	 */
	private fun sortThree(buffer: CircularBuffer): List<Move> {
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
	 * It pushes the smallest elements to B, sorts the remaining 3
	 * elements using [sortThree], and restores the pushed elements
	 * to A. May use 1-2 more moves than A*, but is much faster.
	 *
	 * Time complexity: O(1) (fixed number of cases and moves).
	 *
	 * Space complexity: O(k), k = number of moves returned.
	 *
	 * @return List of moves to sort the stack.
	 */
	private fun sortFourFive(stack: Stack): List<Move> {
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
		return stack.moves.toList()
	}
}