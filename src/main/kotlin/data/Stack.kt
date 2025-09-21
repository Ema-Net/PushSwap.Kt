package me.emaryllis.data

class Stack(
	val a: CircularBuffer, val b: CircularBuffer,
	val chunk: Chunk, private val moveList: MutableList<Move>
) {
	var moves: MutableList<Move> = moveList
		get() = moveList
		private set
	var currentCost = moveList.size
		get() = moveList.size
		private set
	var heuristic: Int = calculateHeuristic()
		get() = moveList.size + currentCost
		private set

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

	fun calculateHeuristic(): Int {
		// TODO()
		return 0
	}
}