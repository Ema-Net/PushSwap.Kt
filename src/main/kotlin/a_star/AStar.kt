package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.getCostInfo
import me.emaryllis.a_star.HeuristicUtil.getMoveInfo
import me.emaryllis.a_star.HeuristicUtil.getStackInfo
import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class AStar {
	private val pullHeuristic = PullHeuristic()
	private val pushHeuristic = PushHeuristic()
	private val states = BestStates()

	fun sort(stack: Stack): Stack {
		var newStack = stack.clone()
		var oldStack = newStack.clone() // Debug
		newStack = aStar(newStack, Move.pushAllowed, pushHeuristic::calculate)

		// Debug info
		println("Pushed with ${newStack.moves.size - oldStack.moves.size} moves, ${getStackInfo(newStack, false)}")
		println(getMoveInfo(newStack, oldStack))

		oldStack = newStack.clone()
		newStack = aStar(newStack, Move.pullAllowed, pullHeuristic::calculate)

		// Debug info
		println("Pulled: ${getStackInfo(newStack, false)}")
		println(getMoveInfo(newStack, oldStack))

		return newStack
	}

	private fun aStar(stack: Stack, allowedMoves: List<Move>, computeHeuristics: (Stack) -> Int): Stack {
		val openList = PriorityQueue()
		openList.push(stack)
		val visited = mutableListOf<Int>() // Hash of visited states

		// Debug variables
		val visitedOrder = mutableMapOf<Int, Int>() // Hash of visited states with iteration count
		var iteration = 0

		while (openList.isNotEmpty()) {
			iteration++
			val currentStack = openList.pop()
			println("I: $iteration, Size: ${openList.size}, ${getStackInfo(currentStack)}, " +
							"OpenList(${openList.size})}: ${getCostInfo(openList.value)}")
			if (canExit(currentStack, allowedMoves, computeHeuristics)) {
				return currentStack
			}

			val hash = currentStack.hashCode()
			if (hasVisited(hash, visited, visitedOrder, iteration)) {
				System.err.println("Skipping visited state (Iteration ${visitedOrder[hash]})")
				continue
			}
			if (states.canPull(currentStack, openList, allowedMoves, computeHeuristics)) continue

			val bestStates = states.getBestStates(currentStack, allowedMoves, computeHeuristics)
			bestStates.forEach { openList.push(it) }
		}
		error("Failed to find a solution")
	}

	/**
	 * Check if we can exit the A* search.
	 * Conditions:
	 * - If pushing phase, all chunk values are in B
	 * - If pulling phase, B is empty
	 */
	private fun canExit(stack: Stack, allowedMoves: List<Move>, computeHeuristics: (Stack) -> Int): Boolean {
		if ((!Move.isPush(allowedMoves) || !stack.b.containsAll(stack.chunk.values)) &&
			(!Move.isPull(allowedMoves) || stack.b.isNotEmpty())
		) return false
		stack.heuristic = computeHeuristics(stack)
		return true
	}

	private fun hasVisited(
		hashCode: Int, visited: MutableList<Int>, visitedOrder: MutableMap<Int, Int>, iteration: Int
	): Boolean {
		if (hashCode in visited) return true
		visited.add(hashCode)
		visitedOrder[hashCode] = iteration
		return false
	}
}