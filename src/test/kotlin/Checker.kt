import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Move

class Checker(private val moves: List<Move>, numList: List<Int>, private val expectedNumList: List<Int>) {
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

	init {
		if (moves.isEmpty()) {
			if (numList.isEmpty() || numList == numList.sorted()) println("OK")
			else System.err.println("Error")
		} else if (!checker()) println("KO")
		else println("OK")
	}

	private fun checker(): Boolean {
		moves.forEach {
			if (opsMap[it]?.invoke() == null) {
				System.err.println("Failed to invoke operation: $it")
				return false
			}
		}
		if (!b.isEmpty) {
			System.err.println("Stack B is not empty. Size: ${b.size}")
			return false
		}
		val status = a.value.toList() == expectedNumList
		if (!status) {
			System.err.println("Expected: $expectedNumList, Got: ${a.value.toList()}")
		}
		return status
	}
}