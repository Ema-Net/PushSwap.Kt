package me.emaryllis.a_star

import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.data.Stack

object HeuristicUtil {
	/**
	 * Returns the number of elements in the buffer that are in its place
	 */
	fun noOfSorted(buffer: CircularBuffer): Int {
		val sorted = buffer.sorted()
		return buffer.withIndex().count { (index, value) -> value == sorted[index] }
	}

	/**
	 * Counts the number of inversions in a list of integers.
	 * An inversion is a pair of elements where the earlier element is
	 * greater than the later one. The function returns this as an integer:
	 * - A score of 0 means the list is fully sorted in ascending order.
	 * - A lower score indicates a more sorted list.
	 * - A higher score indicates a less sorted list.
	 *
	 * The maximum score is (n² - n) / 2, where n is the number of elements in the list.
	 * This occurs when the list is sorted in descending order.
	 *
	 * Time complexity: O(n²)
	 * Space complexity: O(1)
	 */
	fun sortednessScore(list: List<Int>, reverse: Boolean = false): Int {
		var inversions = 0
		for (i in list.indices) {
			for (j in i + 1 until list.size) {
				if (!reverse && list[i] > list[j]) inversions++
				else if (reverse && list[i] < list[j]) inversions++
			}
		}
		return inversions
	}

	/**
	 * Checks if performing a double rotation (RR or RRR) would improve the sortedness score.
	 * If it does, generate a bonus score equal to the improvement.
	 * Otherwise, returns 0.
	 *
	 * Time complexity: O(2n² + n) = O(n²)
	 * - Clone the stack (O(n))
	 * - Calculate sortednessScore for both a and b (O(n²) each)
	 *
	 * Space complexity: O(n) (Stack clone)
	 */
	fun doubleOpsBonus(stack: Stack, oldScore: Int, move: Move): Int {
		val newStack = stack.clone()
		newStack.apply(move)
		val newScore = sortednessScore(newStack.a.value) + sortednessScore(newStack.b.value, true)
		if (newScore > oldScore) {
			return newScore - oldScore // bonus for double rotation
		}
		return oldScore
	}

	fun getStackInfo(stack: Stack, moves: Boolean = true): String = "Stack A: ${stack.a.value}, B: ${stack.b.value}, Chunk: ${stack.chunk.first.minValue}-${stack.chunk.first.maxValue}, " +
			"g: ${stack.moves.size}, h: ${stack.heuristic}, f: ${stack.currentCost}, ${if (moves) "Moves: ${stack.moves}" else ""}"
}
