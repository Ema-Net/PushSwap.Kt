package tests

import Checker
import Settings.DEBUG
import Utils.permutations
import Utils.suppressAllOutput
import me.emaryllis.chunk.ChunkSort
import me.emaryllis.data.Move
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class MainTest {
	private val chunkSort = ChunkSort()

	companion object {
		@JvmStatic
		fun smallSortTest(): Stream<Arguments> = listOf(6)
			.flatMap { size -> (1..size).toList().permutations().toList() }
			.map { Arguments.of(it) }
//			.subList(1, 2)
			.stream()
	}

	private fun check(numList: List<Int>): Pair<Boolean, List<Move>> {
		val moves = chunkSort.chunkSort(numList)
		val status = Checker(moves, numList, numList.sorted()).boolOutput()
		return Pair(status, moves)
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun verifyErrorTest(numList: List<Int>) {
		if (DEBUG) return check(numList) as Unit
		val moves = suppressAllOutput(::check, numList).second
		println("Solved $numList in ${moves.size} moves: $moves.")
	}
}