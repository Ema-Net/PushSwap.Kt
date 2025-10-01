import Settings.DEBUG
import me.emaryllis.chunk.ChunkSort
import me.emaryllis.data.Move
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

object Utils {
	fun String.showNewline() = this.replace("\n", "\\n")
	fun List<Int>.permutations(): Sequence<List<Int>> = sequence {
		if (size < 2) yield(this@permutations)
		else {
			indices.forEach { i ->
				val rest = this@permutations.take(i) + this@permutations.drop(i + 1)
				rest.permutations().forEach { yield(listOf(this@permutations[i]) + it) }
			}
		}
	}

	fun List<Int>.nthPermutation(n: Int): List<Int> {
		val items = this.toMutableList()
		var k = n
		val result = mutableListOf<Int>()
		for (i in items.size downTo 1) {
			val f = (1 until i).fold(1) { acc, v -> acc * v }
			val idx = k / f
			result.add(items.removeAt(idx))
			k %= f
		}
		return result.toList()
	}

	fun <T, R> suppressAllOutput(assertFunc: (T) -> (R), assertParam: T): R {
		val outContent = ByteArrayOutputStream()
		val errContent = ByteArrayOutputStream()
		val originalOut = System.out
		val originalErr = System.err
		try {
			System.setOut(PrintStream(outContent))
			System.setErr(PrintStream(errContent))
			return assertFunc(assertParam)
		} finally {
			System.setOut(originalOut)
			System.setErr(originalErr)
		}
	}

	fun generalCheck(chunkSort: ChunkSort, numList: List<Int>): Pair<Boolean, List<Move>> {
		val moves = chunkSort.chunkSort(numList)
		val status = Checker(moves, numList, numList.sorted()).boolOutput()
		return Pair(status, moves)
	}

	fun generalTest(check: (List<Int>) -> Pair<Boolean, List<Move>>, numList: List<Int>) {
		val (status, moves) = if (DEBUG) check(numList) else suppressAllOutput(check, numList)
		assertTrue(status, "Failed to sort $numList with moves $moves.")
		println("Solved $numList in ${moves.size} moves: $moves.")
	}
}