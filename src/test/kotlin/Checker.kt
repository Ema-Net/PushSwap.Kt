import Settings.DEBUG
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move

class Checker(private val moves: List<Move>, private val numList: List<Int>, private val expectedNumList: List<Int>) {
	private val a = CircularBuffer(numList.size, numList)
	private val b = CircularBuffer(numList.size)
	private val opsMap = mapOf(
		Move.SA to { a.swap() },
		Move.SB to { b.swap() },
		Move.SS to { a.swap(); b.swap() },
		Move.PA to { b.push(a) },
		Move.PB to { a.push(b) },
		Move.RA to { a.rotate() },
		Move.RB to { b.rotate() },
		Move.RR to { a.rotateBoth(b) },
		Move.RRA to { a.reverseRotate() },
		Move.RRB to { b.reverseRotate() },
		Move.RRR to { a.reverseRotateBoth(b) }
	)

	private fun checker(): Boolean? {
		val keys = opsMap.keys
		if (moves.any { it !in keys }) {
			if (DEBUG) System.err.print("Invalid move found: $moves")
			return null
		}
		if (numList.size != numList.toSet().size || expectedNumList.size != expectedNumList.toSet().size) return null
		if (moves.isEmpty()) {
			if (numList.isEmpty() || numList == numList.sorted()) return true
			else if (expectedNumList != expectedNumList.sorted()) error("This permutation is never possible.")
		}
		moves.forEach {
			if (opsMap[it]?.invoke() != true) {
				if (DEBUG) System.err.print("Failed to execute move: $it. Stack A: ${a.toList()}, Stack B: ${b.toList()}.|")
				return false
			}
		}
		if (b.isNotEmpty()) {
			if (DEBUG) System.err.print("Stack B is not empty. Size: ${b.size}|")
			return false
		}
		val status = a.value.toList() == expectedNumList
		if (!status && DEBUG) {
			System.err.print("Expected: $expectedNumList, Got: ${a.value.toList()}.|")
		}
		return status
	}

	fun boolOutput(): Boolean {
		return checker() ?: false
	}

	fun output() {
		when (checker()) {
			null -> System.err.println("Error")
			true -> println("OK")
			false -> System.err.println("KO")
		}
	}
}