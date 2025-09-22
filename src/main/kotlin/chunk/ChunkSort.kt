package me.emaryllis.chunk

import me.emaryllis.Settings.MAX_CHUNK_SIZE
import me.emaryllis.a_star.AStar
import me.emaryllis.data.Chunk
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Stack
import java.lang.Integer.min

class ChunkSort(private val numList: List<Int>) {
	private val aStar = AStar()
	private val chunks: List<Chunk> = defineChunkValues()
	val stack = Stack(CircularBuffer(numList.size, numList), CircularBuffer(numList.size), chunks.first(), mutableListOf())

	fun chunkSort() {
		if (numList == numList.sorted()) return
		if (numList.size <= 5) return SmallSort().smallSort(stack).forEach(::println)
		chunks.forEach { chunk ->
			stack.chunk = chunk
			val moves = aStar.sort(stack)
			println("Chunk ${chunk.minValue} - ${chunk.maxValue} sorted with ${moves.size} moves")
			moves.forEach(stack::apply)
			println("Moves(${moves.size}): ${moves.map { it.toString() }}, A: ${stack.a.value}, B: ${stack.b.value}")
		}
	}

	private fun defineChunkValues(): List<Chunk> {
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
}