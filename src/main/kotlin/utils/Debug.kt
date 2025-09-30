package me.emaryllis.utils

import me.emaryllis.Settings.DEBUG
import me.emaryllis.data.Stack

object Debug {
	fun getMoveInfo(newStack: Stack, oldStack: Stack): String {
		return if (DEBUG) "Moves(${newStack.moves.size - oldStack.moves.size}): ${
			newStack.moves.toList().subList(
				oldStack.moves.size,
				newStack.moves.size
			)
		}" else ""
	}

	fun getCostInfo(stack: Stack): String =
		if (DEBUG) "g: ${stack.moves.size}, h: ${stack.heuristic}, f: ${stack.currentCost}" else ""

	fun getStackInfo(stack: Stack, moves: Boolean = true): String {
		return if (DEBUG) "A: ${stack.a.value}, B: ${stack.b.value}, Chunk: ${stack.chunk.minValue}-${stack.chunk.maxValue}, " +
				"${getCostInfo(stack)}, ${if (moves) "Moves(${stack.moves.size}): ${stack.moves.toList()}" else ""}" else ""
	}
}