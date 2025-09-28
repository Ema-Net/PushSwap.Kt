package me.emaryllis.a_star

import me.emaryllis.Settings.DEBUG
import me.emaryllis.a_star.HeuristicUtil.getStackInfo
import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class BestStates {
	fun getBestStates(
		originalStack: Stack,
		allowedMoves: List<Move>,
		computeHeuristics: (Stack) -> Int,
	): List<Stack> {
		val possibleStates = mutableListOf<Stack>()
		for (move in allowedMoves) {
			val currentStack = applyMoveIfValid(originalStack, move, computeHeuristics)
			if (currentStack != null) {
				if (DEBUG) println(
					"\nApplied move: $move (Valid moves: $allowedMoves)\n" +
							"Before:\t${getStackInfo(originalStack)}\nAfter:\t${getStackInfo(currentStack)}"
				)
				possibleStates.add(currentStack)
			}
		}
		return findBestStates(possibleStates)
	}

	/**
	 * Fast checks for invalid moves to skip unnecessary cloning and applying.
	 * Conditions:
	 * - Inverse move (e.g. PA followed by PB)
	 * - SS move that swaps previous chunk values in A (if prevChunkNum is set)
	 * - PB move when A is empty or first num of A is not in current chunk
	 */
	private fun invalidFast(
		originalStack: Stack,
		move: Move
	): Boolean {
		if (originalStack.moves.isNotEmpty() && originalStack.moves.lastOrNull() == move.inverse()) {
			if (DEBUG) System.err.println("Invalid move: $move. (Inverse)")
			return true
		}
		if (move == Move.SS && originalStack.prevChunkNum != null && originalStack.a.isNotEmpty()
			&& (originalStack.a[0] <= originalStack.prevChunkNum!! || originalStack.a[1] <= originalStack.prevChunkNum!!)
		) {
			if (DEBUG) System.err.println("Invalid move: $move. (Swapping previous chunk values)")
			return true
		}
		if (move == Move.PB && (originalStack.a.isEmpty() || originalStack.a.first() !in originalStack.chunk)) {
			if (DEBUG) System.err.println(
				"Invalid move: $move. Cannot push non-chunk numbers. " +
						"Got: ${originalStack.a.firstOrNull()}. Expected: ${originalStack.chunk.values}"
			)
			return true
		}
		return false
	}

	/**
	 * Returns all best states after applying all allowed moves to the original stack.
	 * Invalid states (inverse moves or Int.MIN_VALUE heuristics) are skipped.
	 */
	private fun applyMoveIfValid(
		originalStack: Stack,
		move: Move,
		computeHeuristics: (Stack) -> Int
	): Stack? {
		// Fast checks (inverse, chunk, etc.)
		if (invalidFast(originalStack, move)) return null

		val stack = originalStack.clone()
		if (!stack.apply(move)) {
			if (DEBUG) System.err.println("Invalid move: $move.")
			return null
		}
		if (move == Move.SS && stack.a.size > 3 && stack.a[0] > stack.a[1]) {
			if (DEBUG) System.err.println("Invalid move: $move. (SS ruined order in A)")
			return null
		}
		stack.heuristic = computeHeuristics(stack)
		if (stack.heuristic < 0) {
			if (DEBUG) System.err.println("Skipping state with negative heuristic after move: $move")
			return null
		}
		return stack
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
			|| (stack.a.isNotEmpty() && stack.prevChunkNum != null && stack.a.last() != stack.prevChunkNum)
		) return false
		if (DEBUG) println("Forcing PA. (B[0] is aligned)")
		stack.apply(Move.PA)
		stack.heuristic = computeHeuristics(stack)
		if (DEBUG) println("After: ${getStackInfo(stack)}")
		openList.push(stack)
		return true
	}

	private fun findBestStates(states: List<Stack>): List<Stack> {
		var minScore = Int.MAX_VALUE
		val result = mutableListOf<Stack>()
		for (s in states) {
			val cost = s.currentCost
			if (cost < minScore) {
				minScore = cost
				result.clear()
				result.add(s)
			} else if (cost == minScore) {
				result.add(s)
			}
		}
		return result
	}
}