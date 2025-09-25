package tests

import Checker
import Utils.permutations
import me.emaryllis.chunk.ChunkSort
import org.junit.jupiter.api.Assertions.assertTrue
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
			.subList(0, 20)
			.stream()
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun verifyErrorTest(numList: List<Int>) {
		assertTrue(Checker(chunkSort.chunkSort(numList), numList, numList.sorted()).boolOutput())
	}
}