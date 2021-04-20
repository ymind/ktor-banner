package team.yi.kfiglet.test

import java.io.File
import org.junit.jupiter.api.Test
import team.yi.kfiglet.FigFont
import team.yi.kfiglet.FigletRenderer

class FigFontsTest {
    private val resourceFolder = File(FigFont::class.java.classLoader.getResource("fonts")!!.path)

    private fun loadFonts(): Map<File, FigFont> {
        val fonts = mutableMapOf<File, FigFont>()

        resourceFolder.listFiles()?.toList().orEmpty()
            .sortedBy { it.name }
            .forEachIndexed { index, file ->
                try {
                    file.inputStream().use {
                        fonts[file] = FigFont.loadFigFont(it)
                    }
                } catch (e: Exception) {
                    println("ERROR: $index, ${file.name}")

                    return@forEachIndexed
                }
            }

        return fonts
    }

    @Test
    fun generateFields() {
        val codeContents = StringBuilder()
        val fonts = this.loadFonts()

        fonts.forEach { (file, font) ->
            val figletRenderer = FigletRenderer(font)
            val banner = figletRenderer.renderText(file.nameWithoutExtension)
            val fontName = file.nameWithoutExtension.toUpperCase()
                .replace("-", "_")
                .replace(" ", "_")
                .replace("'S", "")
                .replace("3_D", "3D")
                .let { if (Character.isDigit(it[0])) "F_$it" else it }
            val title = "${fontName}_FLF"

            val bannerLines = banner
                .replace("*/", "+/")
                .replace("/*", "/+")
                .replace("\\u", "\\o")
                .lines()
            val maxLength = bannerLines.maxOf { it.length }
            val sep = "".padEnd(maxLength, '-')

            codeContents.appendLine("    /**")
            codeContents.appendLine("     * The [$title] FIGfont.")
            codeContents.appendLine("     * ")
            codeContents.appendLine("     * ")
            codeContents.appendLine("     * Example output:")
            codeContents.appendLine("     * ")
            codeContents.appendLine("     * ")
            codeContents.appendLine("     * ```")
            codeContents.appendLine("     * $sep")
            codeContents.appendLine(bannerLines.joinToString("\n") { "     * $it" })
            codeContents.appendLine("     * $sep")
            codeContents.appendLine("     * ```")
            codeContents.appendLine("     */")

            codeContents.appendLine("    const val $title = \"${file.name}\"")
            codeContents.appendLine()
        }

        val outputFile = File("CodeContents.kt")

        outputFile.writeText(codeContents.toString())
    }
}
