package me.emaryllis.data

enum class Move {
	SA, // Only used in small sort
	SB, // Only used in checker test
	SS, // Only used in pushing b to a
	PA, PB, RA, RB, RR, RRA, RRB, RRR;

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

		val duoMoves = listOf(SS, RR, RRR)
	}

	override fun toString() = this.name.lowercase()
	fun inverse() = inverseMap[this] ?: error("Inverse for $this not found")
}