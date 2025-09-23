package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.doubleOpsBonus
import me.emaryllis.a_star.HeuristicUtil.sortednessScore
import me.emaryllis.data.Move
import me.emaryllis.data.Stack

class PullHeuristic {

	/**
	 * Calculates a heuristic score for the given stack state.
	 * Calculates if its better to use double operations (SS, RR, RRR)
	 * to improve the sortedness of both stacks at once.
	 */
	fun calculate(stack: Stack): Int {
		val aScore = sortednessScore(stack.a.value)
		val score = aScore + sortednessScore(stack.b.value, true)
		val (minCost, indices) = bestSortIndicesDescendingB(stack)
		if (minCost * indices.size == 1) return 1 + stack.b.size
		return (minCost * indices.size) + stack.b.size + aScore * 2 + listOf(Move.SS, Move.RR, Move.RRR).sumOf { doubleOpsBonus(stack, score, it) }
	}

	/**
	 * Finds the indices in stack B that are out of descending order
	 * and can be sorted with the least moves (rotations + swap).
	 * @return Pair(minimum cost, indices list).
	 */
	private fun bestSortIndicesDescendingB(stack: Stack): Pair<Int, List<Int>> {
		val bLen = stack.b.size
		var minCost = Int.MAX_VALUE
		val bestIndices = mutableListOf<Int>()
		stack.b.forEachIndexed { idx, x ->
			// In descending order, x should be >= all elements after it
			val outOfOrder = stack.b.drop(idx + 1).any { x < it }
			if (outOfOrder) {
				val cost = minOf(idx, bLen - idx) + 1 // rotations + swap
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