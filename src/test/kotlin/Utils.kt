object Utils {
	fun String.showNewline() = this.replace("\n", "\\n")
	fun List<Int>.permutations(): Sequence<List<Int>> = sequence {
		if (size <= 1) yield(this@permutations)
		else {
			indices.forEach { i ->
				val rest = this@permutations.take(i) + this@permutations.drop(i + 1)
				rest.permutations().forEach { yield(listOf(this@permutations[i]) + it) }
			}
		}
	}
}