package me.emaryllis.a_star

import me.emaryllis.data.Stack

class PullHeuristic {

	fun calculate(stack: Stack): Int {
		if (stack.b.isEmpty()) return Int.MIN_VALUE
		return rotationsToTop(stack) + 1
	}

	private fun rotationsToTop(stack: Stack): Int {
		val max = stack.b.maxOrNull() ?: return Int.MIN_VALUE
		return minOf(max, stack.b.size - max)
	}

}