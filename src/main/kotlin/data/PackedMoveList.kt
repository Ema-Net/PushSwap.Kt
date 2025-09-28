package me.emaryllis.data

/**
 * Stores a list of Move as a bit-packed array (4 bits per move, up to 16 moves per Long).
 */
class PackedMoveList private constructor(
	private val data: LongArray,
	override val size: Int
) : Cloneable, Collection<Move> {
	companion object {
		private const val BITS_PER_MOVE = 4
		private const val MOVES_PER_LONG = 64 / BITS_PER_MOVE // 16
		private const val MOVE_MASK = 0xF.toLong()

		fun empty(): PackedMoveList = PackedMoveList(LongArray(1), 0)
		fun from(moves: List<Move>): PackedMoveList {
			val arr = LongArray((moves.size + MOVES_PER_LONG - 1) / MOVES_PER_LONG)
			for ((i, move) in moves.withIndex()) {
				val arrIdx = i / MOVES_PER_LONG
				val bitIdx = (i % MOVES_PER_LONG) * BITS_PER_MOVE
				arr[arrIdx] = arr[arrIdx] or (move.ordinal.toLong() shl bitIdx)
			}
			return PackedMoveList(arr, moves.size)
		}
	}

	fun add(move: Move): PackedMoveList {
		val newSize = size + 1
		val arrLen = (newSize + MOVES_PER_LONG - 1) / MOVES_PER_LONG
		val newArr = if (arrLen > data.size) data.copyOf(arrLen) else data.copyOf()
		val arrIdx = size / MOVES_PER_LONG
		val bitIdx = (size % MOVES_PER_LONG) * BITS_PER_MOVE
		newArr[arrIdx] = newArr[arrIdx] or (move.ordinal.toLong() shl bitIdx)
		return PackedMoveList(newArr, newSize)
	}

	fun toList(): List<Move> {
		val result = ArrayList<Move>(size)
		for (i in indices) {
			val arrIdx = i / MOVES_PER_LONG
			val bitIdx = (i % MOVES_PER_LONG) * BITS_PER_MOVE
			val ord = ((data[arrIdx] shr bitIdx) and MOVE_MASK).toInt()
			result.add(Move.entries[ord])
		}
		return result
	}

	public override fun clone(): PackedMoveList = PackedMoveList(data.copyOf(), size)
	override fun contains(element: Move): Boolean {
		for (i in indices) {
			val arrIdx = i / MOVES_PER_LONG
			val bitIdx = (i % MOVES_PER_LONG) * BITS_PER_MOVE
			val ord = ((data[arrIdx] shr bitIdx) and MOVE_MASK).toInt()
			if (Move.entries[ord] == element) return true
		}
		return false
	}

	override fun containsAll(elements: Collection<Move>): Boolean {
		for (e in elements) {
			if (!contains(e)) return false
		}
		return true
	}

	override fun isEmpty(): Boolean = size == 0

	override fun iterator(): Iterator<Move> = object : Iterator<Move> {
		private var idx = 0
		override fun hasNext(): Boolean = idx < size
		override fun next(): Move {
			if (!hasNext()) throw NoSuchElementException()
			val arrIdx = idx / MOVES_PER_LONG
			val bitIdx = (idx % MOVES_PER_LONG) * BITS_PER_MOVE
			val ord = ((data[arrIdx] shr bitIdx) and MOVE_MASK).toInt()
			idx++
			return Move.entries[ord]
		}
	}
}
