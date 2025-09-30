package me.emaryllis.data

enum class Move {
	SA, // Only used in small sort or conditional optimization
	SB, SS, // Only used conditionally (not part of branching search)
	PA, PB, RA, RB, RR, RRA, RRB, RRR,
	DONOTUSEONLYFORTESTING; // Used to test invalid move in CheckerTest

	companion object {
		private val inverseMap = mapOf(
			SA to SA,
			SB to SB,
			SS to SS,
			PA to PB,
			PB to PA,
			RA to RRA,
			RB to RRB,
			RR to RRR,
			RRA to RA,
			RRB to RB,
			RRR to RR
		)
		val mixedAllowed = listOf(PA, PB, RA, RB, RR, RRA, RRB, RRR)
	}

	override fun toString() = this.name.lowercase()
	fun inverse() = inverseMap[this] ?: error("Inverse for $this not found")
}