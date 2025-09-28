package me.emaryllis.data

import me.emaryllis.Settings.DEBUG
import me.emaryllis.Settings.HASH_PRIME

class Stack(
	val a: CircularBuffer,
	val b: CircularBuffer,
	var chunk: Chunk,
	var prevChunkNum: Int?,
	var moves: PackedMoveList = PackedMoveList.empty(),
	var heuristic: Int = Int.MAX_VALUE
) : Cloneable {
	// Returns the move history as a list (from first to last)
	val currentCost: Int
		get() = moves.size + heuristic

	public override fun clone(): Stack = Stack(
		a.clone(),
		b.clone(),
		chunk,
		prevChunkNum,
		moves.clone(),
		heuristic
	)

	/**
	 * Had to use stupid hacky way of assigning temp variables for moves that affect both stacks
	 * to prevent compiler from not executing both functions when the first returns false.
	 */
	fun apply(move: Move, log: Boolean = true): Boolean {
		val temp1: Boolean
		val temp2: Boolean
		val status = when (move) {
			Move.SA -> a.swap()
			Move.SB -> b.swap()
			Move.SS -> {
				temp1 = a.swap()
				temp2 = b.swap()
				temp1 && temp2
			}

			Move.PA -> b.push(a)
			Move.PB -> a.push(b)
			Move.RA -> a.rotate()
			Move.RB -> b.rotate()
			Move.RR -> {
				temp1 = a.rotate()
				temp2 = b.rotate()
				temp1 && temp2
			}

			Move.RRA -> a.reverseRotate()
			Move.RRB -> b.reverseRotate()
			Move.RRR -> {
				temp1 = a.reverseRotate()
				temp2 = b.reverseRotate()
				temp1 && temp2
			}
		}
		if (status) {
			moves = moves.add(move)
		} else if (log && DEBUG) System.err.println("Invalid move: $move.")
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