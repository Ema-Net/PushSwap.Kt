package tests

import Checker
import Utils.showNewline
import me.emaryllis.data.Move
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.stream.Stream

class CheckerTest {
	companion object {
		@JvmStatic
		fun checkerTest(): Stream<Arguments> = Stream.of(
			Arguments.of(listOf(Move.SA), listOf(2, 1, 3), listOf(1, 2, 3), "OK\n"),
			Arguments.of(listOf(Move.RA), listOf(1, 2, 3), listOf(2, 3, 1), "OK\n"),
			Arguments.of(listOf(Move.RRA), listOf(1, 2, 3), listOf(3, 1, 2), "OK\n"),
			Arguments.of(listOf(Move.PB, Move.PA), listOf(1, 2, 3), listOf(1, 2, 3), "OK\n"),
			Arguments.of(listOf(Move.PB, Move.PB, Move.PA, Move.PA), listOf(1, 2, 3), listOf(1, 2, 3), "OK\n"),
			Arguments.of(listOf(Move.PB, Move.PB, Move.SB, Move.PA, Move.PA), listOf(1, 2, 3), listOf(2, 1, 3), "OK\n"),
			Arguments.of(listOf(Move.PB, Move.PB, Move.RRA, Move.PA, Move.PA), listOf(1, 2, 3, 4), listOf(1, 2, 4, 3), "OK\n"),
			Arguments.of(listOf(Move.PB, Move.PB, Move.PB, Move.RRR, Move.PA, Move.PA, Move.PA), listOf(1, 2, 3, 4, 5, 6), listOf(2, 3, 1, 6, 4, 5), "OK\n"),
		)
	}

	@Test
	fun checkerNoMovesTest() {
		val expected = "Error\n"

		val errContent = ByteArrayOutputStream()
		val originalErr = System.err
		try {
			System.setErr(PrintStream(errContent))
			Checker(listOf(), listOf(2, 1, 3), listOf(2, 1, 3))
			val stdout = errContent.toString().replace("\r", "")
			assertTrue(stdout.contains(expected), "Expected: '${expected.showNewline()}', but got: '${stdout.showNewline()}'")
		} finally {
			System.setErr(originalErr)
		}
	}

	@ParameterizedTest
	@MethodSource("checkerTest")
	fun verifyCheckerTest(moves: List<Move>, numList: List<Int>, expectedNumList: List<Int>, expected: String) {
		val outContent = ByteArrayOutputStream()
		val originalOut = System.out
		try {
			System.setOut(PrintStream(outContent))
			Checker(moves, numList, expectedNumList)
			val stderr = outContent.toString().replace("\r", "")
			assertTrue(stderr.contains(expected), "Expected: '${expected.showNewline()}', but got: '${stderr.showNewline()}'")
		} finally {
			System.setOut(originalOut)
		}
	}
}