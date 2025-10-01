package tests

import Utils.generalCheck
import Utils.generalTest
import me.emaryllis.chunk.ChunkSort
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Stream

class DescendingTest {
	private val chunkSort = ChunkSort()

	companion object {
		private val failed = AtomicBoolean(false)

		@JvmStatic
		fun descendingTest(): Stream<Arguments> = perms(100, 100)

		@Suppress("SameParameterValue")
		private fun perms(from: Int, until: Int): Stream<Arguments> {
			require(from <= until) { "From must be less than or equal to until." }
			return (from..until).map { Arguments.of((it downTo 1).toList()) }.stream()
		}
	}

	@ParameterizedTest
	@MethodSource("descendingTest")
	fun descendingTest(numList: List<Int>) {
		Assumptions.assumeFalse(failed.get())
		generalTest({ generalCheck(chunkSort, it) }, numList)
	}
}