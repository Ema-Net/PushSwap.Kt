package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.getStackInfo
import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class BestStates {
	/**
	 * Returns all best states after applying all allowed moves to the original stack.
	 * Invalid states (inverse moves or Int.MIN_VALUE heuristics) are skipped.
	 */
	fun getBestStates(
		originalStack: Stack,
		allowedMoves: List<Move>,
		computeHeuristics: (Stack) -> Int,
	): List<Stack> {
		val possibleStates = mutableListOf<Stack>()
		for (move in allowedMoves) {
			val currentStack = originalStack.clone()

			if (isInvalid(currentStack, move, computeHeuristics)) continue

			println(
				"\nApplied move: $move (Valid moves: $allowedMoves)\n" +
						"Before:\t${getStackInfo(originalStack)}\nAfter:\t${getStackInfo(currentStack)}"
			)
			possibleStates.add(currentStack)
		}
		return findBestStates(possibleStates)
	}

	/**
	 * Conditions:
	 * - Is pulling phase
	 * - If first num of B is max of B (Since pushing is FILO, so it requires B to be in descending order)
	 * - If first num of A is the last number of the previous chunk
	 * (If A is not empty - Edge case for 1 chunk when all num are in B)
	 *
	 * If met, applies PA and returns true.
	 * This is a forced move, so the heuristic is recalculated.
	 * @return true if PA was applied, else false.
	 * */
	fun canPull(
		stack: Stack,
		openList: PriorityQueue,
		allowedMoves: List<Move>,
		computeHeuristics: (Stack) -> Int
	): Boolean {
		if (!Move.isPull(allowedMoves) || stack.b.isEmpty() || stack.b.first() != stack.b.maxOrNull()
			|| (stack.a.isNotEmpty() && stack.prevChunkNum != null && stack.b.last() != stack.prevChunkNum)
		) return false
		println("Forcing PA. (B[0] is aligned)")
		stack.apply(Move.PA)
		stack.heuristic = computeHeuristics(stack)
		openList.push(stack)
		return true
	}

	private fun isInvalid(stack: Stack, move: Move, computeHeuristics: (Stack) -> Int): Boolean {
		// Check for inverse and invalid moves
		if (stack.moves.isNotEmpty() && stack.moves.lastOrNull() == move.inverse()) {
			System.err.println("Invalid move: $move. (Inverse)")
			return true
		}
		if (!stack.apply(move)) return true

		// Invalid heuristic states (Int.MIN_VALUE)
		stack.heuristic = computeHeuristics(stack)
		if (stack.heuristic < 0) {
			System.err.println("Skipping state with negative heuristic after move: $move")
			return true
		}
		return false
	}

	/**
	 * Returns all states with the minimum score ([Stack.currentCost]).
	 * Edge Case: If all moves are invalid, states can be empty, so minScore can be null.
	 */
	private fun findBestStates(states: List<Stack>): List<Stack> {
		val minScore = states.minByOrNull { it.currentCost }?.currentCost
		return if (minScore != null) states.filter { it.currentCost == minScore } else emptyList()
	}
}