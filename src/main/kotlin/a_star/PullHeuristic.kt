package me.emaryllis.a_star

import me.emaryllis.a_star.HeuristicUtil.minRotIdxB
import me.emaryllis.data.Stack

class PullHeuristic {
	fun calculate(stack: Stack): Int {
		val b = stack.b
		if (b.isEmpty()) return Int.MIN_VALUE
		val maxB = b.maxOrNull() ?: return Int.MIN_VALUE
		return minRotToBottom(stack) + minRotIdxB(b, maxB) + 1
	}

	private fun minRotToBottom(stack: Stack): Int {
		val idx = stack.a.indexOf(stack.prevChunkNum)
		if (idx == -1) return 0
		val size = stack.a.size
		return minOf((size - 1 - idx) % size, (idx + 1) % size)
	}
}