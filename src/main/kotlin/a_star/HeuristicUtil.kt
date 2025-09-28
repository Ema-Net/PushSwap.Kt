package me.emaryllis.a_star

import me.emaryllis.Settings.DEBUG
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.data.Stack
import me.emaryllis.data.VirtualRotatedBuffer

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

	private val minRotIdxBCache = mutableMapOf<Pair<Int, Int>, Int>()

	/**
	 * Computes the minimal number of single-stack rotations needed to bring stack B
	 * into the best position for inserting x at the top, such that the resulting stack
	 * is as close as possible to descending order.
	 *
	 * For each possible rotation of B, simulates inserting x at the head and evaluates
	 * the sortedness. Returns the minimal number of rotations (forward or reverse)
	 * required to achieve the best sortedness after insertion.
	 *
	 * @param b The current stack B as a list (head at index 0).
	 * @param x The value to be pushed onto B.
	 * @return The minimal rotation count (forward or reverse) to achieve the best
	 *         sortedness after inserting x at the top.
	 * @see HeuristicUtil.sortednessScore
	 *
	 * Time complexity: O(m³ + 2m²) = O(m³), where m = B's size.
	 * Space complexity: O(m).
	 */
	fun minRotIdxB(b: CircularBuffer, x: Int): Int {
		if (b.isEmpty()) return 0
		if (x >= b[0]) return 0
		val cacheKey = b.hashCode() to x
		minRotIdxBCache[cacheKey]?.let { return it }
		var bestScore = Int.MAX_VALUE
		var bestRot = 0
		val size = b.size
		for (i in 0 until size) {
			val newB = VirtualRotatedBuffer(b, x, i)
			val score = sortednessScore(newB, true)
			if (score < bestScore) {
				bestScore = score
				bestRot = i
				if (score == 0) break // Early exit if perfectly sorted
			}
		}
		val result = minOf(bestRot, size - bestRot)
		minRotIdxBCache[cacheKey] = result
		return result
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
		newStack.apply(move, log = false)
		val newScore = sortednessScore(newStack.a.value) + sortednessScore(newStack.b.value, true)
		if (newScore > oldScore) {
			return newScore - oldScore // bonus for double rotation
		}
		return oldScore
	}

	fun getMoveInfo(newStack: Stack, oldStack: Stack): String {
		return if (DEBUG) "Moves(${newStack.moves.size - oldStack.moves.size}): ${
			newStack.moves.toList().subList(
				oldStack.moves.size,
				newStack.moves.size
			)
		}" else ""
	}

	fun getCostInfo(stack: Stack): String =
		if (DEBUG) "g: ${stack.moves.size}, h: ${stack.heuristic}, f: ${stack.currentCost}" else ""

	fun getStackInfo(stack: Stack, moves: Boolean = true): String {
		return if (DEBUG) "A: ${stack.a.value}, B: ${stack.b.value}, Chunk: ${stack.chunk.minValue}-${stack.chunk.maxValue}, " +
				"${getCostInfo(stack)}, ${if (moves) "Moves(${stack.moves.size}): ${stack.moves.toList()}" else ""}" else ""
	}
}
