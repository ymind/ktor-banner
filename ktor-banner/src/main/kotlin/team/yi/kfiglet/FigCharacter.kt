package team.yi.kfiglet

/**
 * FigCharacter represents a single FIGlet character from a FIGfont.
 *
 * Constructs a new instance of [FigCharacter].
 *
 * @param font          The font of which this FigCharacter is a part.
 * @param characterData The character data that defines this characters appearance.
 */
@Suppress("SpellCheckingInspection")
class FigCharacter internal constructor(private val font: FigFont, private val characterData: String) {
    /**
     * Returns the FIGcharacter sub-character at the requested column and row.
     *
     * @param column The column for which to return a sub-character.
     * @param row    The row for which to return a sub-character.
     * @return The sub-character at the requested column and row.
     * @throws IndexOutOfBoundsException if the requested column and row does not exist within the
     * FIGcharacter data.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    @Throws(IndexOutOfBoundsException::class)
    fun getCharacterAt(column: Int, row: Int): Char {
        return if (column in 0 until width && row >= 0 && row < height) {
            characterData[row * width + column]
        } else {
            throw IndexOutOfBoundsException("Character index out of bounds: $column, $row")
        }
    }

    /**
     * Returns string of sub-characters that define a row of the FIGcharacter.
     *
     * @param row The row for which to return the sub-character string.
     * @return The sub-characters that define the requested FIGcharacter row.
     * @throws IndexOutOfBoundsException if the requested row does not exist within the FIGcharacter data.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun getRow(row: Int): String {
        return if (row in 0 until height) {
            val rowStart = row * width

            characterData.substring(rowStart, rowStart + width)
        } else {
            throw IndexOutOfBoundsException(
                "Character row must be between 0 and " + (height - 1) + ": " + row
            )
        }
    }

    val width: Int
        get() = characterData.length / font.height

    @Suppress("MemberVisibilityCanBePrivate")
    val height: Int
        get() = font.height

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        val width = width

        for (y in 0 until font.height) {
            stringBuilder.append(characterData.substring(width * y, width * y + width))
            stringBuilder.append("\n")
        }

        return stringBuilder.toString()
    }
}
