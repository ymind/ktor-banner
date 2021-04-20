package team.yi.kfiglet

/**
 * The direction of printing.
 */
enum class PrintDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT;

    companion object {
        @Throws(IllegalArgumentException::class)
        fun ofHeaderValue(headerValue: Int): PrintDirection {
            return when (headerValue) {
                0 -> LEFT_TO_RIGHT
                1 -> RIGHT_TO_LEFT
                else -> throw IllegalArgumentException("Unrecognised header value: $headerValue")
            }
        }
    }
}
