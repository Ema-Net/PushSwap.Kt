package me.emaryllis.a_star

import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class AStar {
	private val pullHeuristic = PullHeuristic()
	private val pushHeuristic = PushHeuristic()

	fun sort(stack: Stack): List<Move> {
		val newStack = stack.clone()
		val moves = mutableListOf<Move>()
		val pushToB = aStar(newStack, pushHeuristic::calculate)
		moves.addAll(pushToB)
		pushToB.forEach(newStack::apply)
		moves.addAll(aStar(newStack, pullHeuristic::calculate))
		return moves
	}

	private fun aStar(stack: Stack, computeHeuristics: (Stack) -> Unit): List<Move> {
		val openList = PriorityQueue()
		openList.push(stack)
		val visited = mutableListOf<Int>() // Hash of visited states

		// Debug variables
		val visitedOrder = mutableMapOf<Int, Int>() // Hash of visited states with iteration count
		var iteration = 0

		while (!openList.isEmpty) {
			iteration++
			val currentStack = openList.pop()
			// Mark as completed when all chunk values are in stack B
			if (!currentStack.a.containsAll(currentStack.chunk.values)) return currentStack.moves

			val hash = currentStack.hashCode()
			if (hasVisited(hash, visited, visitedOrder, iteration)) {
				System.err.println("Skipping visited state (Iteration ${visitedOrder[hash]})")
				continue
			}
			computeMoves(currentStack, openList, computeHeuristics)
		}
		error("Failed to find a solution")
	}

	private fun hasVisited(hashCode: Int, visited: MutableList<Int>, visitedOrder: MutableMap<Int, Int>, iteration: Int): Boolean {
		if (hashCode in visited) return true
		visited.add(hashCode)
		visitedOrder[hashCode] = iteration
		return false
	}

	private fun computeMoves(originalStack: Stack, openList: PriorityQueue, computeHeuristics: (Stack) -> Unit) {
		for (move in Move.entries) {
			val currentStack = originalStack.clone()
			if (currentStack.moves.isNotEmpty() && currentStack.moves.last() == move.inverse()) {
				println("Skipping invalid move: ${move}. (Inverse)")
				continue
			}
			if (!currentStack.apply(move)) {
				println("Skipping invalid move: $move (Invalid State)")
				continue
			}
			computeHeuristics(currentStack)
			openList.push(currentStack)
		}
	}
}