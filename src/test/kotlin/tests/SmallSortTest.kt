package tests

import Checker
import Utils.showNewline
import me.emaryllis.chunk.SmallSort
import me.emaryllis.data.Chunk
import me.emaryllis.data.CircularBuffer
import me.emaryllis.data.Stack
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.stream.Stream

open class SmallSortTest {
	private val smallSort = SmallSort()

	companion object {
		@JvmStatic
		fun smallSortTest(): Stream<Arguments> = listOf(3, 4, 5)
			.flatMap { size -> (1..size).toList().permutations().toList() }
			.map { Arguments.of(it, "OK\n") }
			.stream()

		// Extension function to generate all permutations of a list
		private fun List<Int>.permutations(): Sequence<List<Int>> = sequence {
			if (size <= 1) yield(this@permutations)
			else {
				indices.forEach { i ->
					val rest = this@permutations.take(i) + this@permutations.drop(i + 1)
					rest.permutations().forEach { yield(listOf(this@permutations[i]) + it) }
				}
			}
		}
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun verifyErrorTest(numList: List<Int>, expected: String = "OK\n") {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		try {
			System.setOut(PrintStream(outContent))
			val stack = Stack(CircularBuffer(numList.size, numList), CircularBuffer(numList.size), Chunk(numList.min(), numList.max(), numList), mutableListOf())
			Checker(smallSort.smallSort(stack), numList, numList.sorted())
			val stdout = outContent.toString().replace("\r", "")
			assertTrue(stdout.contains(expected), "Expected: '${expected.showNewline()}', but got: '${stdout.showNewline()}'")
		} finally {
			System.setOut(originalOut)
		}
	}

	@Test
	fun testAlreadySorted() {
		verifyErrorTest(listOf(1, 2, 3), "")
	}
}