package me.emaryllis.data

/**
 * A virtual, read-only list that simulates inserting a value at the head of a rotated CircularBuffer.
 * No data is copied; all access is via index.
 *
 * @param buffer The CircularBuffer to view.
 * @param insertValue The value to insert at the head.
 * @param rotation The number of positions to rotate the buffer.
 */
class VirtualRotatedBuffer(
    private val buffer: CircularBuffer,
    private val insertValue: Int,
    private val rotation: Int
) : AbstractList<Int>() {
    override val size: Int get() = buffer.size + 1
    override fun get(index: Int): Int = when (index) {
        0 -> insertValue
        else -> buffer[(rotation + index - 1) % buffer.size]
    }
}

