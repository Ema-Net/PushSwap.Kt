package tests

import Utils.showNewline
import me.emaryllis.main
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.stream.Stream

class ErrorTest {
	companion object {
		@JvmStatic
		fun errorTest(): Stream<Arguments> = Stream.of(
			Arguments.of(arrayOf(""), ""),
			Arguments.of(arrayOf(" "), "Error\n"),
			Arguments.of(arrayOf("Hello World"), "Error\n"),
			Arguments.of(arrayOf("3 2 one"), "Error\n"),
		)
	}

	@ParameterizedTest
	@MethodSource("errorTest")
	fun verifyErrorTest(numList: Array<String>, expected: String) {
		val errContent = ByteArrayOutputStream()
		val originalErr = System.err
		try {
			System.setErr(PrintStream(errContent))
			main(numList)
			val stderr = errContent.toString().replace("\r", "")
			assertTrue(stderr.contains(expected), "Expected: '${expected.showNewline()}', but got: '${stderr.showNewline()}'")
		} finally {
			System.setErr(originalErr)
		}
	}
}