package me.emaryllis.a_star

import me.emaryllis.Logger.logger
import me.emaryllis.a_star.HeuristicUtil.getStackInfo
import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class AStar {
	private val pullHeuristic = PullHeuristic()
	private val pushHeuristic = PushHeuristic()

	fun sort(stack: Stack): Stack {
		var newStack = stack.clone()
		var oldStack = newStack.clone() // Debug
		newStack = aStar(newStack, listOf(Move.SA, Move.SB, Move.SS, Move.PA), pushHeuristic::calculate)

		// Debug info
		logger.info("Pushed with ${newStack.moves.size - oldStack.moves.size} moves, ${getStackInfo(newStack, false)}")
		System.err.println(
			"Moves(${newStack.moves.size - oldStack.moves.size}): ${
				newStack.moves.subList(
					oldStack.moves.size,
					newStack.moves.size
				)
			}"
		)

		oldStack = newStack.clone()
		newStack = aStar(newStack, listOf(Move.SA, Move.PA, Move.PB), pullHeuristic::calculate)

		// Debug info
		logger.info("Pulled: ${getStackInfo(newStack, false)}")
		System.err.println(
			"Moves(${newStack.moves.size - oldStack.moves.size}): ${
				newStack.moves.subList(
					oldStack.moves.size,
					newStack.moves.size
				)
			}"
		)

		return newStack
	}

	private fun aStar(stack: Stack, excludedMoves: List<Move>, computeHeuristics: (Stack) -> Int): Stack {
		val openList = PriorityQueue()
		openList.push(stack)
		val visited = mutableListOf<Int>() // Hash of visited states

		// Debug variables
		val visitedOrder = mutableMapOf<Int, Int>() // Hash of visited states with iteration count
		var iteration = 0

		while (openList.isNotEmpty()) {
			iteration++
			val currentStack = openList.pop()
			logger.info("I: $iteration, Size: ${openList.size}, ${getStackInfo(currentStack)}")
			if (excludedMoves.last() == Move.PA && currentStack.b.containsAll(currentStack.chunk.values) ||
				excludedMoves.last() == Move.PB && currentStack.b.isEmpty()
			) {
				currentStack.heuristic = computeHeuristics(stack)
				return currentStack
			}
			val hash = currentStack.hashCode()
			if (hasVisited(hash, visited, visitedOrder, iteration)) {
				System.err.println("Skipping visited state (Iteration ${visitedOrder[hash]})")
				continue
			}
			if (excludedMoves.last() == Move.PB && currentStack.b.first() == currentStack.b.maxOrNull()) {
				System.err.println("Forcing PA. (Empty A or B[0] is aligned)")
				currentStack.apply(Move.PA)
				currentStack.heuristic = computeHeuristics(currentStack)
				openList.push(currentStack)
				continue
			}

			val bestStates = getBestStates(currentStack, excludedMoves, computeHeuristics)
			bestStates.forEach { openList.push(it) }
			//pruneOpenList(openList, bestStates)
		}
		error("Failed to find a solution")
	}

	private fun hasVisited(
		hashCode: Int,
		visited: MutableList<Int>,
		visitedOrder: MutableMap<Int, Int>,
		iteration: Int
	): Boolean {
		if (hashCode in visited) return true
		visited.add(hashCode)
		visitedOrder[hashCode] = iteration
		return false
	}

	private fun getBestStates(
		originalStack: Stack,
		excludedMoves: List<Move>,
		computeHeuristics: (Stack) -> Int
	): List<Stack> {
		val possibleStates = mutableListOf<Stack>()
		for (move in Move.entries.filter { !excludedMoves.contains(it) }) {
			// Restrict PA to only push current chunk values
			if (move == Move.PA) {
				val topB = originalStack.b.value.firstOrNull()
				val currentChunk = originalStack.chunk
				if (topB == null || topB !in currentChunk.values) {
					continue // Do not allow PA if top of B is not in current chunk
				}
			}
			if (move in listOf(
					Move.SS,
					Move.RR,
					Move.RRR
				) && (originalStack.a.size < 2 || originalStack.b.size < 2)
			) continue
			val currentStack = originalStack.clone()
			if (currentStack.moves.isNotEmpty() && currentStack.moves.last() == move.inverse()) {
				//System.err.println("Skipping invalid move: ${move}. (Inverse)")
				continue
			}
			val oldState = currentStack.clone()
			if (!currentStack.apply(move)) {
				//System.err.println("Skipping invalid move: $move (Invalid State)")
				continue
			}
			currentStack.heuristic = computeHeuristics(currentStack)
			// Guard against invalid heuristic values
			if (currentStack.heuristic == Int.MIN_VALUE) {
				System.err.println("Skipping state with invalid heuristic (Int.MIN_VALUE) after move: $move")
				continue
			}
			System.err.println(
				"Applied move: $move | Before: ${getStackInfo(originalStack)} | After: ${
					getStackInfo(
						currentStack
					)
				}"
			)
			if (currentStack.heuristic >= 0) possibleStates.add(currentStack)
		}
		return findBestStates(possibleStates)
	}

	/**
	 * Returns all states with the minimum score ([Stack.heuristic] + Size of[Stack.moves]).
	 * Edge Case: If all moves are invalid, states can be empty, so minScore can be null.
	 */
	private fun findBestStates(states: List<Stack>): List<Stack> {
		val minScore = states.minByOrNull { it.heuristic + it.moves.size }?.let { it.heuristic + it.moves.size }
		return if (minScore != null) states.filter { it.heuristic + it.moves.size == minScore } else emptyList()
	}

	private fun pruneOpenList(openList: PriorityQueue, bestStates: List<Stack>) {
		val minOpenScore = openList.peek().let { it.heuristic + it.moves.size }
		val minBestScore = bestStates.minByOrNull { it.heuristic + it.moves.size }?.let { it.heuristic + it.moves.size }
		val pruneScore = listOfNotNull(minOpenScore, minBestScore).minOrNull()
		if (pruneScore != null) {
			openList.retainAll { it.heuristic + it.moves.size <= pruneScore }
		}
	}
}