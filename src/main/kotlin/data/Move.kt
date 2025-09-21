package me.emaryllis.data

enum class Move {
	SA, // Only used in small sort
	SB, SS, // Only used in checker test
	PA, PB, RA, RB, RR, RRA, RRB, RRR;

	companion object {
		private val inverseMap = mapOf(
			PA to PB,
			PB to PA,
			RA to RRA,
			RB to RRB,
			RR to RRR,
			RRA to RA,
			RRB to RB,
			RRR to RR
		)
	}

	override fun toString() = this.name.lowercase()
	fun inverse() = inverseMap[this] ?: error("Inverse for $this not found")
}