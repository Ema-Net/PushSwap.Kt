package me.emaryllis.data

import me.emaryllis.Settings.HASH_PRIME

class Stack(
	val a: CircularBuffer,
	val b: CircularBuffer,
	var chunk: Pair<Chunk, Chunk?>, // Pair(currentChunk, nextChunk)
	private val moveList: MutableList<Move>,
	var heuristic: Int = Int.MAX_VALUE
) {
	var moves: MutableList<Move> = moveList
		get() = moveList
		private set
	var currentCost = heuristic
		get() = moveList.size + heuristic
		private set

	fun clone(): Stack = Stack(a.clone(), b.clone(), Pair(chunk.first.clone(), chunk.second?.clone()), moveList.toMutableList(), heuristic)

	fun apply(move: Move): Boolean {
		val status = when (move) {
			Move.SA -> a.swap()
			Move.SB -> b.swap()
			Move.SS -> {
				a.swap()
				b.swap()
			}

			Move.PA -> b.push(a)
			Move.PB -> a.push(b)
			Move.RA -> a.rotate()
			Move.RB -> b.rotate()
			Move.RR -> {
				a.rotate()
				b.rotate()
			}

			Move.RRA -> a.reverseRotate()
			Move.RRB -> b.reverseRotate()
			Move.RRR -> {
				a.reverseRotate()
				b.reverseRotate()
			}
		}
		if (status) moves.add(move)
		return status
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Stack) return false
		return hashCode() == other.hashCode()
	}

	override fun hashCode(): Int {
		var result = a.hashCode()
		listOf(b.hashCode(), chunk.first.hashCode(), chunk.second?.hashCode() ?: 0, heuristic).forEach {
			result = HASH_PRIME * result + it
		}
		return result
	}
}