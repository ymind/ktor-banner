package team.yi.kfiglet

import java.io.BufferedReader
import java.io.IOException
import java.io.Reader
import java.util.regex.Pattern

/**
 * FigFontReader reads [FigFont] instances from a [Reader].
 */
class FigFontReader
/**
 * Constructs a new instance of FontReader.
 *
 * @param reader The [Reader] from which to read font data.
 */(private val reader: Reader) {
    /**
     * Reads a [FigFont].
     *
     * @return The [FigFont] read from the [Reader] data.
     * @throws IOException if there is a problem reading the font data.
     */
    @Throws(IOException::class)
    fun readFont(): FigFont {
        val fontBuilder = FigFont.Builder()

        BufferedReader(reader).use { bufferedReader ->
            val header = bufferedReader.readLine()

            try {
                parseHeader(header, fontBuilder)
            } catch (e: IllegalArgumentException) {
                throw IOException("Could not read font header", e)
            }

            // Skip over the comment lines
            for (commentCounter in 0 until fontBuilder.commentLines) {
                bufferedReader.readLine()
            }

            // A FIGfont is required to have characters for ASCII 32 to 126 inclusive
            for (codePoint in 32..126) {
                val characterData = readCharacterData(fontBuilder.height, bufferedReader)

                fontBuilder.setFigCharacter(codePoint.toChar(), characterData)
            }

            // Additional required Deutsch FIGcharacters, in order:
            //
            // 196 (umlauted "A" -- two dots over letter "A")
            // 214 (umlauted "O" -- two dots over letter "O")
            // 220 (umlauted "U" -- two dots over letter "U")
            // 228 (umlauted "a" -- two dots over letter "a")
            // 246 (umlauted "o" -- two dots over letter "o")
            // 252 (umlauted "u" -- two dots over letter "u")
            // 223 ("ess-zed" -- see FIGcharacter illustration below)
            for (codePoint in deutschCodePoints) {
                val characterData = readCharacterData(fontBuilder.height, bufferedReader)

                fontBuilder.setFigCharacter(codePoint.toChar(), characterData)
            }

            // Now there just remains to parse any code tags that have been defined.
            var line: String? = null

            while ((bufferedReader.readLine()?.also { line = it }) != null) {
                val characterData = readCharacterData(fontBuilder.height, bufferedReader)

                try {
                    fontBuilder.setFigCharacter(parseCodeTag(line!!), characterData)
                } catch (e: IllegalArgumentException) {
                    throw IOException("Could not parse code tag", e)
                }
            }
        }

        return fontBuilder.build()
    }

    companion object {
        /**
         * The magic number used to determine if a stream of data contains a FIGfont
         * definition.
         */
        @Suppress("MemberVisibilityCanBePrivate")
        const val FONT_MAGIC_NUMBER = "flf2"

        // Based on http://www.jave.de/docs/figfont.txt
        private val CODE_TAG_PATTERN = Pattern.compile("([^\\s]+)\\s*.*")

        private val deutschCodePoints = intArrayOf(196, 214, 220, 228, 246, 252, 223)

        /**
         * Returns the unicode character represented by a code tag.
         *
         * @param codeTagText The code tag text to parse.
         * @return The character represented.
         * @throws IllegalArgumentException if the text cannot be parsed as a code tag.
         */
        @Throws(IllegalArgumentException::class)
        fun parseCodeTag(codeTagText: String): Char {
            val codeTagMatcher = CODE_TAG_PATTERN.matcher(codeTagText)

            return if (codeTagMatcher.matches()) {
                val codePointText = codeTagMatcher.group(1)
                val codePoint = Integer.decode(codePointText)

                codePoint.toChar()
            } else {
                throw IllegalArgumentException("Could not parse text as a code tag: $codeTagText")
            }
        }

        /**
         * Reads the data that defines a single character from a [BufferedReader].
         *
         * @param height         The height of the character in lines of data.
         * @param bufferedReader The buffered reader from which to read the character data.
         * @return The string that represents the character data.
         * @throws IOException if there is a problem reading the data.
         */
        @Throws(IOException::class)
        fun readCharacterData(height: Int, bufferedReader: BufferedReader): String {
            val stringBuilder = StringBuilder()

            for (charLine in 0 until height) {
                val line = bufferedReader.readLine()
                var charIndex = line.length - 1

                // Skip over any whitespace characters at the end of the line
                while (charIndex >= 0 && Character.isWhitespace(line[charIndex])) charIndex--

                // We've found a non-whitespace character that we will interpret as an
                // end-character.
                val endChar = line[charIndex]

                // Skip over any end-characters.
                while (charIndex >= 0 && line[charIndex] == endChar) charIndex--

                // We've found the right-hand edge of the actual character data for this line.
                stringBuilder.append(line.substring(0, charIndex + 1))
            }

            return stringBuilder.toString()
        }

        /**
         * Parses a FIGfont header into a [FigFont.Builder] instance.
         *
         * @param header      The header to parse.
         * @param fontBuilder The font builder to set with values read from the header.
         * @throws IllegalArgumentException if the header text cannot be parsed as a FIGfont header.
         */
        @Throws(IllegalArgumentException::class)
        fun parseHeader(header: String, fontBuilder: FigFont.Builder) {
            val arguments = header.split("\\s+".toRegex()).toTypedArray()

            if (arguments[0].startsWith(FONT_MAGIC_NUMBER)) {
                fontBuilder.hardBlankChar = arguments[0][arguments[0].length - 1]

                if (arguments.size > 1) fontBuilder.height = Integer.decode(arguments[1]).toInt()
                if (arguments.size > 2) fontBuilder.baseline = Integer.decode(arguments[2]).toInt()
                if (arguments.size > 3) fontBuilder.maxLength = Integer.decode(arguments[3]).toInt()
                if (arguments.size > 4) {
                    val oldLayout = Integer.decode(arguments[4]).toInt()

                    fontBuilder.oldLayout = oldLayout
                    fontBuilder.fullLayout = LayoutOptions.fullLayoutFromOldLayout(oldLayout)
                }
                if (arguments.size > 5) fontBuilder.commentLines = Integer.decode(arguments[5]).toInt()
                if (arguments.size > 6) fontBuilder.direction = PrintDirection.ofHeaderValue(Integer.decode(arguments[6]).toInt())
                if (arguments.size > 7) fontBuilder.fullLayout = Integer.decode(arguments[7]).toInt()
                if (arguments.size > 8) fontBuilder.codeTagCount = Integer.decode(arguments[8]).toInt()
            } else {
                throw IllegalArgumentException("Header does not start with FIGfont magic number $FONT_MAGIC_NUMBER: $header")
            }
        }
    }
}
