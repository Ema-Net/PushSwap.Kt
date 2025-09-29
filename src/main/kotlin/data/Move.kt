package me.emaryllis.data

enum class Move {
	SA, // Only used in small sort
	SB, SS, // Only used in pull phase
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

		// Disallowed PA to avoid pushing already sorted elements
		val pullAllowed = listOf(SB, SS, RA, RB, RR, RRA, RRB, RRR)
		val pushAllowed = listOf(PB, RA, RB, RR, RRA, RRB, RRR)

		// Using this instead of 'in' to reduce time complexity
		fun isPush(allowedMoves: List<Move>): Boolean = allowedMoves.first() == PB
		fun isPull(allowedMoves: List<Move>): Boolean = allowedMoves.first() == SB
	}

	override fun toString() = this.name.lowercase()
	fun inverse() = inverseMap[this] ?: error("Inverse for $this not found")
}