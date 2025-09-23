package tests

import Checker
import Utils.permutations
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
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun smallSortTest(numList: List<Int>, expected: String = "OK\n") {
		val outContent = ByteArrayOutputStream()
		val errContent = ByteArrayOutputStream()
		val originalOut = System.out
		val originalErr = System.err
		try {
			System.setOut(PrintStream(outContent))
			System.setErr(PrintStream(errContent))
			val stack = Stack(CircularBuffer(numList.size, numList), CircularBuffer(numList.size), Pair(Chunk(0, 0, emptyList()), null), mutableListOf())
			Checker(smallSort.smallSort(stack), numList, numList.sorted())
			assertTrue(errContent.toString().trim().isEmpty(), "Expected no error output, but got: '${errContent.toString().showNewline()}'")
			val stdout = outContent.toString().replace("\r", "")
			assertTrue(stdout.contains(expected), "Expected: '${expected.showNewline()}', but got: '${stdout.showNewline()}'")
		} finally {
			System.setOut(originalOut)
			System.setErr(originalErr)
		}
	}

	@Test
	fun noOutputTest() {
		smallSortTest(emptyList(), "")
		smallSortTest(listOf(1), "")
		smallSortTest(listOf(1, 2), "")
		smallSortTest(listOf(1, 2, 3), "")
	}
}