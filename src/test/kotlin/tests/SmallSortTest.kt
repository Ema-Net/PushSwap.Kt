package tests

import Checker
import Utils.showNewline
import me.emaryllis.chunk.SmallSort
import me.emaryllis.data.CircularBuffer
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
		fun smallSortTest(): Stream<Arguments> =
			(1..3).flatMap { i ->
				(1..3).flatMap { j ->
					(1..3).map { k -> Arguments.of(listOf(i, j, k), "OK\n") }
				}
			}
				.filter { (it.get()[0] as List<*>).distinct().size == 3 }
				.filter { it.get()[0] as List<*> != listOf(1, 2, 3) }
				.stream()
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun verifyErrorTest(numList: List<Int>, expected: String = "OK\n") {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		try {
			System.setOut(PrintStream(outContent))
			Checker(smallSort.sortThree(CircularBuffer(numList.size, numList)), numList, numList.sorted())
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