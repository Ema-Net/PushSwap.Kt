package tests

import Utils.generalCheck
import Utils.generalTest
import Utils.nthPermutation
import Utils.permutations
import me.emaryllis.chunk.ChunkSort
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

class PermutationTest {
	private val chunkSort = ChunkSort()

	companion object {
		@JvmStatic
		fun failedPermutation(): Stream<Arguments> = generatePermutations(
			listOf(
				20, 22, 24
			)
		)

		@JvmStatic
		fun allPermutations(): Stream<Arguments> = generatePermutations((1..5040).toList())

		private fun generatePermutations(tests: List<Int> = emptyList()): Stream<Arguments> {
			val size = 14
			val baseList = (1..size).toList()
			return if (tests.isEmpty()) {
				baseList.permutations().map { Arguments.of(it) }.asStream()
			} else {
				tests.map { Arguments.of(baseList.nthPermutation(it - 1)) }.stream()
			}
		}
	}

	@ParameterizedTest
	@MethodSource("allPermutations")
	fun allPermutations(numList: List<Int>) = generalTest({ generalCheck(chunkSort, it) }, numList)

	@ParameterizedTest
	@MethodSource("failedPermutation")
	fun failedPermutations(numList: List<Int>) = allPermutations(numList)
}