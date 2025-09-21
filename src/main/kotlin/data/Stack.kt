package me.emaryllis.data

class Stack(
	val a: CircularBuffer, val b: CircularBuffer,
	val chunk: Chunk, val moves: MutableList<Move>,
	val currentCost: Int, val heuristic: Int
) {
	fun clone(): Stack = Stack(a.clone(), b.clone(), chunk.clone(), moves.toMutableList(), currentCost, heuristic)
}