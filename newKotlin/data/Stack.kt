package me.emaryllis.data

import me.emaryllis.Settings.HASH_PRIME

class Stack(
	val a: CircularBuffer,
	val b: CircularBuffer,
	var chunk: Chunk,
	var prevChunkNum: Int?,
	private val moveList: MutableList<Move>,
	var heuristic: Int = Int.MAX_VALUE
) {
	var moves: MutableList<Move> = moveList
		get() = moveList
		private set
	var currentCost = heuristic
		get() = moveList.size + heuristic
		private set

	fun clone(): Stack = Stack(
		a.clone(),
		b.clone(),
		chunk.clone(),
		prevChunkNum,
		moveList.toMutableList(),
		heuristic
	)

	fun apply(move: Move, log: Boolean = true): Boolean {
		// Debug
		if (move in listOf(Move.SS, Move.RR, Move.RRR) && (a.size <= 1 || b.size <= 1)) {
			if (log) System.err.println("Invalid move: $move. Sizes are too small. Size A: ${a.size}, Size B: ${b.size}")
			return false
		}
		if (move in listOf(Move.SA, Move.RA, Move.RRA) && a.size <= 1) {
			if (log) System.err.println("Invalid move: $move. Size A is too small. Size A: ${a.size}")
			return false
		}
		if (move in listOf(Move.SB, Move.RB, Move.RRB) && b.size <= 1) {
			if (log) System.err.println("Invalid move: $move. Size B is too small. Size B: ${b.size}")
			return false
		}
		///
		val status = when (move) {
			Move.SA -> a.swap()
			Move.SB -> b.swap()
			Move.SS -> a.swap() && b.swap()
			Move.PA -> b.push(a)
			Move.PB -> a.push(b)
			Move.RA -> a.rotate()
			Move.RB -> b.rotate()
			Move.RR -> a.rotate() && b.rotate()
			Move.RRA -> a.reverseRotate()
			Move.RRB -> b.reverseRotate()
			Move.RRR -> a.reverseRotate() && b.reverseRotate()
		}
		if (status) moves.add(move)
		else if (log) System.err.println("Invalid move: $move.")
		return status
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Stack) return false
		return hashCode() == other.hashCode()
	}

	override fun hashCode(): Int {
		var result = a.hashCode()
		listOf(b.hashCode(), chunk.hashCode(), prevChunkNum ?: 0, heuristic).forEach {
			result = HASH_PRIME * result + it
		}
		return result
	}
}