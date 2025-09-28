package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.minRotIdxB
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
	 * m = [minRotIdxB]'s complexity
	 *
	 * Space complexity: O(1) auxiliary.
	 *
	 * @return rotation cost of the cheapest eligible element, and the number of
	 * eligible elements (assuming they can be pushed without any rotations).
	 * @see HeuristicUtil.minRotIdxB
	 */
	private fun pushPhaseLB(stack: Stack): Int {
		if (stack.a.isEmpty()) {
			return if (stack.b.size != stack.b.capacity) Int.MIN_VALUE else 0
		}

		var eligible = 0
		var minRotationCost = Int.MAX_VALUE

		stack.a.forEachIndexed { idx, v ->
			if (v in stack.chunk) {
				eligible++
				val rotA = minOf(idx, stack.a.size - idx)
				val rotB = if (stack.b.isEmpty()) 0 else minRotIdxB(stack.b, v)
				val rotationCost = rotA + rotB
				if (rotationCost < minRotationCost) minRotationCost = rotationCost
			}
		}

		if (eligible == 0) return 0
		if (minRotationCost == Int.MAX_VALUE) minRotationCost = 0
		return eligible + minRotationCost
	}


}
