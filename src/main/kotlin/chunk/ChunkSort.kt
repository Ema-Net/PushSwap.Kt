package me.emaryllis.chunk

import me.emaryllis.Settings.DEBUG
import me.emaryllis.Settings.MAX_CHUNK_SIZE
import me.emaryllis.a_star.AStar
import me.emaryllis.data.Chunk
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move
import me.emaryllis.data.Stack
import me.emaryllis.utils.Debug.getMoveInfo
import me.emaryllis.utils.Debug.getStackInfo
import java.lang.Integer.min

class ChunkSort {
	private val aStar = AStar()

	fun chunkSort(numList: List<Int>): List<Move> {
		if (numList == numList.sorted()) return emptyList()
		val chunks: List<Chunk> = defineChunkValues(numList)
		if (DEBUG) println("Defined ${chunks.size} chunk(s). ${chunks.map { "${it.minValue}-${it.maxValue}:${it.values}" }}")
		var stack = Stack(
			CircularBuffer(numList.size, numList),
			CircularBuffer(numList.size),
			chunks.first(),
			null
		)
		var oldStack: Stack // Debug
		if (numList.size <= 5) return SmallSort().smallSort(stack)
		var i = 0
		while (chunks.size > i) {
			if (i > 0) {
				stack.prevChunkNum = chunks[i - 1].maxValue
			}
			stack.chunk = chunks[i]
			if (DEBUG) println(
				"Sorting chunk ${chunks[i].minValue} - ${chunks[i].maxValue}, ${
					getStackInfo(
						stack,
						false
					)
				}"
			)
			oldStack = stack.clone() // Debug
			stack = aStar.sort(stack)
			if (DEBUG) println("Chunk ${chunks[i].minValue} - ${chunks[i].maxValue} sorted.")
			if (DEBUG) println("New ${getMoveInfo(stack, oldStack)}, ${getStackInfo(stack, false)}")
			i++
		}
		shiftSmallestToTop(stack, chunks.first().minValue)
		if (DEBUG) println("Shifted smallest to top, final ${getStackInfo(stack)}")
		return stack.moves.toList()
	}

	private fun defineChunkValues(numList: List<Int>): List<Chunk> {
		val sorted = numList.sorted()
		val chunkSize = min(MAX_CHUNK_SIZE, sorted.size)
		val noOfChunks = (sorted.size + chunkSize - 1) / chunkSize
		val chunks = mutableListOf<Chunk>()

		var startIdx = 0
		for (i in 0 until noOfChunks) {
			val size = min(chunkSize, sorted.size - startIdx)
			val chunkValues = sorted.subList(startIdx, startIdx + size)
			val minValue = chunkValues.first()
			val maxValue = chunkValues.last()
			chunks.add(Chunk(minValue, maxValue, chunkValues))
			startIdx += size
		}
		return chunks
	}

	/**
	 * Returns the minimal sequence of moves to shift the smallest element in stack A to the top.
	 * Used to shift the smallest element to the top after all chunks have been sorted and pushed back to A.
	 * Time complexity: O(n)
	 * Space complexity: O(k) where k = number of moves
	 */
	private fun shiftSmallestToTop(stack: Stack, minValue: Int) {
		val aList = stack.a.value
		val minIdx = stack.a.indexOf(minValue)
		if (minIdx == -1) error("$minValue not found in A: ${stack.a.value}") // Debug check
		else if (minIdx == 0) return // already at top
		if (minIdx <= aList.size / 2) {
			repeat(minIdx) { stack.apply(Move.RA) }
		} else {
			repeat(aList.size - minIdx) { stack.apply(Move.RRA) }
		}
	}
}