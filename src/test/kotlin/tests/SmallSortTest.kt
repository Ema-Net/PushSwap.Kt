package tests

import Checker
import Utils.permutations
import me.emaryllis.chunk.SmallSort
import me.emaryllis.data.Chunk
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Stack
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

open class SmallSortTest {
	private val smallSort = SmallSort()

	companion object {
		@JvmStatic
		fun smallSortTest(): Stream<Arguments> = listOf(3, 4, 5)
			.flatMap { size -> (1..size).toList().permutations().toList() }
			.map { Arguments.of(it) }
			.stream()
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun smallSortTest(numList: List<Int>) {
		val stack = Stack(
			CircularBuffer(numList.size, numList),
			CircularBuffer(numList.size),
			Pair(Chunk(0, 0, emptyList()), null),
			mutableListOf()
		)
		assertTrue(Checker(smallSort.smallSort(stack), numList, numList.sorted()).boolOutput())
	}

	@Test
	fun noOutputTest() {
		smallSortTest(emptyList())
		smallSortTest(listOf(1))
		smallSortTest(listOf(1, 2))
		smallSortTest(listOf(1, 2, 3))
	}
}