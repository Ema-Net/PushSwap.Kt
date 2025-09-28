package me.emaryllis.a_star

import me.emaryllis.Settings.DEBUG
import me.emaryllis.a_star.HeuristicUtil.getMoveInfo
import me.emaryllis.a_star.HeuristicUtil.getStackInfo
import me.emaryllis.data.Chunk
import me.emaryllis.data.Move
import me.emaryllis.data.PriorityQueue
import me.emaryllis.data.Stack

class AStar {
	private val pullHeuristic = PullHeuristic()
	private val pushHeuristic = PushHeuristic()
	private val states = BestStates()

	fun sort(stack: Stack): Stack {
		if (inSortedOrder(stack) && stack.chunk.maxValue != stack.a.maxOrNull()!!) {
			if (DEBUG) println("Chunk ${stack.chunk.minValue} - ${stack.chunk.maxValue} is already in sorted order.")
			return stack
		}
		var newStack = stack.clone()
		var oldStack = newStack.clone() // Debug
		newStack = aStar(newStack, Move.pushAllowed, pushHeuristic::calculate)
		newStack.heuristic = pullHeuristic.calculate(newStack)

		// Debug info
		if (DEBUG) println("Pushed: ${getStackInfo(newStack, false)} ${getMoveInfo(newStack, oldStack)}")

		oldStack = newStack.clone()
		newStack = aStar(newStack, Move.pullAllowed, pullHeuristic::calculate)

		// Debug info
		if (DEBUG) println("Pulled: ${getStackInfo(newStack, false)} ${getMoveInfo(newStack, oldStack)}")

		return newStack
	}

	private fun aStar(stack: Stack, allowedMoves: List<Move>, computeHeuristics: (Stack) -> Int): Stack {
		val openList = PriorityQueue()
		openList.push(stack)
		val visited = mutableSetOf<Int>() // Hash of visited states

		// Debug variables
		val visitedOrder = mutableMapOf<Int, Int>() // Hash of visited states with iteration count
		var iteration = 0

		while (openList.isNotEmpty()) {
			iteration++
			val currentStack = openList.pop()
			if (DEBUG) println("\nI: $iteration, Size: ${openList.size}, ${getStackInfo(currentStack)}, " +
																"OpenList(${openList.size})")
			if (canExit(currentStack, allowedMoves, computeHeuristics)) {
				return currentStack
			}

			val hash = currentStack.hashCode()
			if (hasVisited(hash, visited, visitedOrder, iteration)) {
				if (DEBUG) System.err.println("Skipping visited state (Iteration ${visitedOrder[hash]})")
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
		hashCode: Int, visited: MutableSet<Int>, visitedOrder: MutableMap<Int, Int>, iteration: Int
	): Boolean {
		if (!visited.add(hashCode)) return true // add returns false if already present
		visitedOrder[hashCode] = iteration
		return false
	}

	/**
	 * Checks if all chunk elements in A are in sorted order.
	 * Used to exit early during pushing phase if A is already sorted.
	 *
	 * Time complexity: O(n)
	 *
	 * Space complexity: O(k) -> [Chunk.values]'s size
	 */
	private fun inSortedOrder(stack: Stack): Boolean {
		val values = stack.a.filter { it in stack.chunk }
		return values == values.sorted()
	}
}