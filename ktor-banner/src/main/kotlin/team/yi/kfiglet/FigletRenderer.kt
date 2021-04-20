package team.yi.kfiglet

import kotlin.streams.asSequence

/**
 * FigletRenderer renders text as FIGlet text.
 */
class FigletRenderer(private val figFont: FigFont) {
    /**
     * Renders text as FIGlet text.
     *
     * @param text The text to render.
     * @return The rendered FIGlet text as a multi-line string.
     */
    @Suppress("ComplexMethod", "LongMethod", "NestedBlockDepth", "ComplexCondition")
    fun renderText(text: String, smushMode: Int = figFont.fullLayout, direction: PrintDirection = figFont.direction): String {
        val result = StringBuilder()
        val rowBuilders: MutableList<StringBuilder> = MutableList(figFont.height) { StringBuilder() }
        var prevChar = '\u0000'

        for (char in text.toCharArray()) {
            var character = char

            // Treat tabs and spaces as spaces, and all other whitespace characters as
            // newlines.
            if (Character.isWhitespace(character)) character = if (character == '\t' || character == ' ') ' ' else '\n'

            // Skip over unprintable characters. '\u0000'..' '
            if (character in ''..'' && character != '\n' || character.toInt() == 127) continue

            prevChar = if (character != '\n') {
                val smushAmount = figFont.calculateOverlapAmount(prevChar, character, smushMode, direction)
                val figChar = figFont.getFigCharacter(character)

                for (row in 0 until figFont.height) {
                    val rowBuilder = rowBuilders[row]

                    if (rowBuilder.isNotEmpty()) {
                        if (direction == PrintDirection.LEFT_TO_RIGHT) {
                            // Smush the new FIGcharacter onto the right of the previous FIGcharacter.
                            for (smushColumn in 0 until smushAmount) {
                                val smushIndex = rowBuilder.length - (smushColumn + 1)

                                rowBuilder.setCharAt(
                                    smushIndex,
                                    figFont.smushem(
                                        rowBuilder[smushIndex],
                                        figChar.getCharacterAt(smushAmount - (smushColumn + 1), row),
                                        smushMode,
                                        direction,
                                    )
                                )
                            }

                            rowBuilder.append(figChar.getRow(row).substring(smushAmount))
                        } else {
                            // Smush the new FIGcharacter into the left of the previous FIGcharacter.
                            for (smushColumn in 0 until smushAmount) {
                                rowBuilder.setCharAt(
                                    smushColumn,
                                    figFont.smushem(
                                        rowBuilder[smushColumn],
                                        figChar.getCharacterAt(figChar.width - smushAmount + smushColumn, row),
                                        smushMode,
                                        direction,
                                    )
                                )
                            }

                            rowBuilder.insert(0, figChar.getRow(row).substring(0, figChar.width - smushAmount))
                        }
                    } else {
                        rowBuilder.append(figChar.getRow(row))
                    }
                }

                character
            } else {
                // We've encountered a newline. We need to render the current buffer and then
                // start a new one.
                result.append(
                    rowBuilders.stream()
                        .asSequence()
                        .map { rowBuilder: StringBuilder -> rowBuilder.toString() }
                        .map { s: String -> s.replace(figFont.hardBlankChar, ' ') }
                        .joinToString("\n", prefix = "", postfix = "\n")
                )

                for (row in 0 until figFont.height) {
                    rowBuilders[row].setLength(0)
                }

                '\u0000'
            }
        }

        result.append(
            rowBuilders.stream()
                .asSequence()
                .map { rowBuilder: StringBuilder -> rowBuilder.toString() }
                .map { s: String -> s.replace(figFont.hardBlankChar, ' ') }
                .joinToString("\n")
        )

        return result.toString()
    }
}
