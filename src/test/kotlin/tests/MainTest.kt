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
		fun failedTests(): Stream<Arguments> = allPermutations(
			listOf(121, 127, 145, 151, 169, 175, 241, 247,
					25, 265, 271, 289, 290, 295, 296, 31, 361,
					367, 368, 385, 386, 391, 392, 409, 410, 415, 416, 49, 55, 7))

		@JvmStatic
		fun allTests(): Stream<Arguments> = allPermutations()

		private fun allPermutations(tests: List<Int> = emptyList()): Stream<Arguments> {
			val permutations = listOf(7)
				.flatMap { size -> (1..size).toList().permutations().toList() }
			if (tests.isEmpty()) return permutations.map { Arguments.of(it) }.stream()
			return tests.map { Arguments.of(permutations[it - 1]) }.subList(0, 300).stream()
		}
	}

	private fun check(numList: List<Int>): Pair<Boolean, List<Move>> {
		val moves = chunkSort.chunkSort(numList)
		val status = Checker(moves, numList, numList.sorted()).boolOutput()
		return Pair(status, moves)
	}

	@ParameterizedTest
	@MethodSource("allTests")
	fun runAll(numList: List<Int>) {
		if (DEBUG) {
			check(numList)
			return
		}
		val moves = suppressAllOutput(::check, numList).second
		println("Solved $numList in ${moves.size} moves: $moves.")
	}

	@ParameterizedTest
	@MethodSource("failedTests")
	fun runFailed(numList: List<Int>) = runAll(numList)
}