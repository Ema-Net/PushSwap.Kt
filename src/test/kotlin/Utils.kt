import java.io.ByteArrayOutputStream
import java.io.PrintStream

object Utils {
	fun String.showNewline() = this.replace("\n", "\\n")
	fun List<Int>.permutations(): Sequence<List<Int>> = sequence {
		if (size < 2) yield(this@permutations)
		else {
			indices.forEach { i ->
				val rest = this@permutations.take(i) + this@permutations.drop(i + 1)
				rest.permutations().forEach { yield(listOf(this@permutations[i]) + it) }
			}
		}
	}

	fun List<Int>.nthPermutation(n: Int): List<Int> {
		val items = this.toMutableList()
		var k = n
		val result = mutableListOf<Int>()
		for (i in items.size downTo 1) {
			val f = (1 until i).fold(1) { acc, v -> acc * v }
			val idx = k / f
			result.add(items.removeAt(idx))
			k %= f
		}
		return result.toList()
	}

	fun <T, R> suppressAllOutput(assertFunc: (T) -> (R), assertParam: T): R {
		val outContent = ByteArrayOutputStream()
		val errContent = ByteArrayOutputStream()
		val originalOut = System.out
		val originalErr = System.err
		try {
			System.setOut(PrintStream(outContent))
			System.setErr(PrintStream(errContent))
			return assertFunc(assertParam)
		} finally {
			System.setOut(originalOut)
			System.setErr(originalErr)
		}
	}
}