package tests

import Checker
import Utils.permutations
import Utils.showNewline
import me.emaryllis.chunk.ChunkSort
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.stream.Stream

class MainTest {
	private val chunkSort = ChunkSort()

	companion object {
		@JvmStatic
		fun smallSortTest(): Stream<Arguments> = listOf(6)
			.flatMap { size -> (1..size).toList().permutations().toList() }
			.map { Arguments.of(it, "OK\n") }
//			.subList(200, 300)
			.stream()
	}

	@ParameterizedTest
	@MethodSource("smallSortTest")
	fun verifyErrorTest(numList: List<Int>, expected: String = "OK\n") {
		val outContent = ByteArrayOutputStream()
		val errContent = ByteArrayOutputStream()
		val originalOut = System.out
		val originalErr = System.err
		try {
			System.setOut(PrintStream(outContent))
			System.setErr(PrintStream(errContent))
			Checker(chunkSort.chunkSort(numList), numList, numList.sorted())
			val stdout = outContent.toString().replace("\r", "")
			assertTrue(stdout.contains(expected), "Expected: ${expected.showNewline()}, but got: ${stdout.showNewline()}")
		} finally {
			System.setOut(originalOut)
			System.setErr(originalErr)
		}
	}
}