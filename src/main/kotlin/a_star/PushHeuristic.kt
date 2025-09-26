package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.sortednessScore
import me.emaryllis.data.Stack

class PushHeuristic {
	fun calculate(stack: Stack): Int {
		if (stack.a.isEmpty() && stack.b.size != stack.b.capacity) return Int.MIN_VALUE
		return pushPhaseLB(stack)
	}

	/**
	 * Lower bound on remaining cost to push all chunk elements
	 * from A to B while keeping B descending (optimistically).
	 *
	 * Time complexity: (k elements eligible) O(n + k * m),
	 * worst case (all elements eligible): O(n * m).
	 * - n = A's size, k = no of chunk elements in A (k <= n),
	 * m = minRotB's complexity
	 *
	 * Space complexity: O(1) auxiliary.
	 *
	 * @return rotation cost of the cheapest eligible element, and the number of
	 * eligible elements (assuming they can be pushed without any rotations).
	 * @see PushHeuristic.minRotB
	 */
	private fun pushPhaseLB(stack: Stack): Int {
		if (stack.a.isEmpty()) {
			return if (stack.b.size != stack.b.capacity) Int.MIN_VALUE else 0
		}

		var eligible = 0
		var minRotationCost = Int.MAX_VALUE

		stack.a.forEachIndexed { idx, v ->
			if (v in stack.chunk.minValue..stack.chunk.maxValue) {
				eligible++
				val rotA = minOf(idx, stack.a.size - idx)
				val rotB = if (stack.b.isEmpty()) 0 else minRotB(stack.b.value, v)
				val rotationCost = rotA + rotB
				if (rotationCost < minRotationCost) minRotationCost = rotationCost
			}
		}

		if (eligible == 0) return 0
		if (minRotationCost == Int.MAX_VALUE) minRotationCost = 0
		return eligible + minRotationCost
	}

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
	private fun minRotB(b: List<Int>, x: Int): Int {
		if (b.isEmpty()) return 0
		if (x >= b[0]) return 0
		var bestScore = Int.MAX_VALUE
		var bestRot = 0
		for (i in b.indices) {
			val rotated = b.drop(i) + b.take(i) // Rotate b by i
			val newB = mutableListOf(x)
			newB.addAll(rotated)
			val score = sortednessScore(newB, true)
			if (score < bestScore) {
				bestScore = score
				bestRot = i
			}
		}
		return minOf(bestRot, b.size - bestRot)
	}
}
