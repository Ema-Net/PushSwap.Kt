package me.emaryllis.chunk

import me.emaryllis.Settings.MAX_CHUNK_SIZE
import me.emaryllis.data.Chunk
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Stack
import java.lang.Integer.min

class ChunkSort(private val numList: List<Int>) {
	val chunks: List<Chunk> = defineChunkValues()
	val smallSort = SmallSort()
	val stack = Stack(CircularBuffer(numList.size, numList), CircularBuffer(numList.size), chunks.first(), mutableListOf())

	fun chunkSort() {
		if (numList == numList.sorted()) return
		if (numList.size <= 5) return smallSort.smallSort(stack).forEach(::println)
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