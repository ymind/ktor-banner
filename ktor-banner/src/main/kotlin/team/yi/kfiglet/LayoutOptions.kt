package team.yi.kfiglet

@Suppress("SpellCheckingInspection", "unused")
object LayoutOptions {
    /**
     * Rule 1: EQUAL CHARACTER SMUSHING (code value 1)
     *
     *
     * Two sub-characters are smushed into a single sub-character if they are the
     * same. This rule does not smush hardblanks.
     */
    const val HORIZONTAL_EQUAL_CHARACTER_SMUSHING = 1 shl 0

    /**
     * Rule 2: UNDERSCORE SMUSHING (code value 2)
     *
     *
     * An underscore ("_") will be replaced by any of: "|", "/", "\", "[", "]", "{",
     * "}", "(", ")", "&lt;" or "&gt;".
     */
    const val HORIZONTAL_UNDERSCORE_SMUSHING = 1 shl 1

    /**
     * Rule 3: HIERARCHY SMUSHING (code value 4)
     *
     *
     * A hierarchy of six classes is used: "|", "/\", "[]", "{}", "()", and
     * "&lt;&gt;". When two smushing sub-characters are from different classes, the
     * one from the latter class will be used.
     */
    const val HORIZONTAL_HIERARCHY_SMUSHING = 1 shl 2

    /**
     * Rule 4: OPPOSITE PAIR SMUSHING (code value 8)
     *
     *
     * Smushes opposing brackets ("[]" or "]["), braces ("{}" or "}{") and
     * parentheses ("()" or ")(") together, replacing any such pair with a vertical
     * bar ("|").
     */
    const val HORIZONTAL_OPPOSITE_PAIR_SMUSHING = 1 shl 3

    /**
     * Rule 5: BIG X SMUSHING (code value 16)
     *
     *
     * Smushes "/\" into "|", "\/" into "Y", and "&gt;&lt;" into "X". Note that
     * "&lt;&gt;" is not smushed in any way by this rule. The name "BIG X" is
     * historical; originally all three pairs were smushed into "X".
     */
    const val HORIZONTAL_BIG_X_SMUSHING = 1 shl 4

    /**
     * Rule 6: HARDBLANK SMUSHING (code value 32)
     *
     *
     * Smushes two hardblanks together, replacing them with a single hardblank.
     */
    const val HORIZONTAL_HARDBLANK_SMUSHING = 1 shl 5

    /**
     * Moves FIGcharacters closer together until they touch. Typographers use the
     * term "kerning" for this phenomenon when applied to the horizontal axis, but
     * fitting also includes this as a vertical behavior, for which there is
     * apparently no established typographical term.
     */
    const val HORIZONTAL_FITTING_BY_DEFAULT = 1 shl 6

    /**
     * Moves FIGcharacters one step closer after they touch, so that they partially
     * occupy the same space. A FIGdriver must decide what sub-character to display
     * at each junction. There are two ways of making these decisions: by controlled
     * smushing or by universal smushing.
     */
    const val HORIZONTAL_SMUSHING_BY_DEFAULT = 1 shl 7

    /**
     * Rule 1: EQUAL CHARACTER SMUSHING (code value 256)
     *
     *
     * Same as horizontal smushing rule 1.
     */
    const val VERTICAL_EQUAL_CHARACTER_SMUSHING = 1 shl 8

    /**
     * Rule 2: UNDERSCORE SMUSHING (code value 512)
     *
     *
     * Same as horizontal smushing rule 2.
     */
    const val VERTICAL_UNDERSCORE_SMUSHING = 1 shl 9

    /**
     * Rule 3: HIERARCHY SMUSHING (code value 1024)
     *
     *
     * Same as horizontal smushing rule 3.
     */
    const val VERTICAL_HIERARCHY_SMUSHING = 1 shl 10

    /**
     * Rule 4: HORIZONTAL LINE SMUSHING (code value 2048)
     *
     *
     * Smushes stacked pairs of "-" and "_", replacing them with a single "="
     * sub-character. It does not matter which is found above the other. Note that
     * vertical smushing rule 1 will smush IDENTICAL pairs of horizontal lines,
     * while this rule smushes horizontal lines consisting of DIFFERENT
     * sub-characters.
     */
    const val VERTICAL_HORIZONTAL_LINE_SMUSHING = 1 shl 11

    /**
     * Rule 5: VERTICAL LINE SUPERSMUSHING (code value 4096)
     *
     *
     * This one rule is different from all others, in that it "supersmushes"
     * vertical lines consisting of several vertical bars ("|"). This creates the
     * illusion that FIGcharacters have slid vertically against each other.
     * Supersmushing continues until any sub-characters other than "|" would have to
     * be smushed. Supersmushing can produce impressive results, but it is seldom
     * possible, since other sub-characters would usually have to be considered for
     * smushing as soon as any such stacked vertical lines are encountered.
     */
    const val VERTICAL_VERTICAL_LINE_SMUSHING = 1 shl 12

    /**
     * Moves FIGcharacters closer together until they touch. Typographers use the
     * term "kerning" for this phenomenon when applied to the horizontal axis, but
     * fitting also includes this as a vertical behavior, for which there is
     * apparently no established typographical term.
     */
    const val VERTICAL_FITTING_BY_DEFAULT = 1 shl 13

    /**
     * Moves FIGcharacters one step closer after they touch, so that they partially
     * occupy the same space. A FIGdriver must decide what sub-character to display
     * at each junction. There are two ways of making these decisions: by controlled
     * smushing or by universal smushing.
     */
    const val VERTICAL_SMUSHING_BY_DEFAULT = 1 shl 14

    fun isLayoutOptionSelected(layoutOption: Int, layoutValue: Int) = layoutValue and layoutOption != 0

    /**
     * Converts an old layout value (Legal values -1 to 63) into the equivalent full
     * layout value.
     *
     * <dl>
     * <dt>-1</dt>
     * <dd>Full-width layout by default</dd>
     * <dt>0</dt>
     * <dd>Horizontal fitting (kerning) layout by default</dd>
     * <dt>1</dt>
     * <dd>Apply horizontal smushing rule 1 by default</dd>
     * <dt>2</dt>
     * <dd>Apply horizontal smushing rule 2 by default</dd>
     * <dt>4</dt>
     * <dd>Apply horizontal smushing rule 3 by default</dd>
     * <dt>8</dt>
     * <dd>Apply horizontal smushing rule 4 by default</dd>
     * <dt>16</dt>
     * <dd>Apply horizontal smushing rule 5 by default</dd>
     * <dt>32</dt>
     * <dd>Apply horizontal smushing rule 6 by default</dd>
     * </dl>
     *
     * @param oldLayout The old layout value to convert into a full layout value.
     * @return The full layout value.
     */
    fun fullLayoutFromOldLayout(oldLayout: Int): Int {
        return when (oldLayout) {
            -1 -> 0
            0 -> HORIZONTAL_FITTING_BY_DEFAULT
            else -> oldLayout
        }
    }
}
