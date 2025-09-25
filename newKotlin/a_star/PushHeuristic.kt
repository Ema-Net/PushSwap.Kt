package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.doubleOpsBonus
import me.emaryllis.a_star.HeuristicUtil.sortednessScore
import me.emaryllis.data.Move
import me.emaryllis.data.Stack

class PushHeuristic {
	/**
	 * Calculates a heuristic score for the given stack state.
	 */
	fun calculate(stack: Stack): Int {
		if (stack.a.isEmpty() && stack.b.size != stack.b.capacity) return Int.MIN_VALUE // error state
		val score = sortednessScore(stack.a.value) + sortednessScore(stack.b.value, true)

		val (minCost, indices) = bestPushIndices(stack)
		println("Heuristic: MinCost: $minCost, Indices: $indices, Score: $score")
		return (minCost * indices.size) + listOf(Move.RR, Move.RRR).sumOf { doubleOpsBonus(stack, score, it) }
	}

	/**
	 * Finds the indices of the elements in stack A that belong to the current chunk
	 * and can be pushed to stack B with the least cost (rotations + push).
	 * If multiple elements have the same minimum cost, all their indices are returned.
	 *
	 * Time complexity: O(n) -> n = size of stack A
	 * Space complexity: O(k) -> k = no of indices found
	 *
	 * @return Pair(minimum cost, indices list)
	 */
	private fun bestPushIndices(stack: Stack): Pair<Int, List<Int>> {
		val aLen = stack.a.size

		var minCost = Int.MAX_VALUE
		val bestIndices = mutableListOf<Int>()

		stack.a.forEachIndexed { idx, x ->
			if (x in stack.chunk.minValue..stack.chunk.maxValue) {
				val cost = minOf(idx, aLen - idx) + 1 // rotations + push
				when {
					cost < minCost -> {
						minCost = cost
						bestIndices.clear()
						bestIndices.add(idx)
					}

					cost == minCost -> bestIndices.add(idx)
				}
			}
		}
		return Pair(minCost, bestIndices)
	}
}
