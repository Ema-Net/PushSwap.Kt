package me.emaryllis.data

import me.emaryllis.Settings.HASH_PRIME

class Stack(
	val a: CircularBuffer, val b: CircularBuffer,
	var chunk: Chunk, private val moveList: MutableList<Move>
) {
	var moves: MutableList<Move> = moveList
		get() = moveList
		private set
	var currentCost = moveList.size
		get() = moveList.size
		private set
	var heuristic: Int = Int.MAX_VALUE

	fun clone(): Stack = Stack(a.clone(), b.clone(), chunk.clone(), moveList.toMutableList())

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
		listOf(b.hashCode(), chunk.hashCode(), heuristic).forEach {
			result = HASH_PRIME * result + it
		}
		return result
	}
}