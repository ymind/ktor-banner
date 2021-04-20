package team.yi.ktor.features

import io.ktor.application.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import team.yi.kfiglet.*

class Banner(private val config: Configuration) {
    @Suppress("unused", "SpellCheckingInspection")
    class Configuration {
        var smushMode: Int? = null
        var direction = PrintDirection.LEFT_TO_RIGHT

        internal var before: (BannerContext) -> Unit = { }
        internal var after: (BannerContext) -> Unit = { }
        internal var render: (BannerContext) -> Unit = { println(it.text) }

        var bannerText = "Ktor"
        var fontName = FigFonts.STANDARD_FLF
        var loadFigFont: (String) -> FigFont = { FigFonts.loadFigFontResource(it) }

        fun beforeBanner(customizer: (BannerContext) -> Unit) {
            before = customizer
        }

        fun afterBanner(customizer: (BannerContext) -> Unit) {
            after = customizer
        }

        fun render(customizer: (BannerContext) -> Unit) {
            render = customizer
        }
    }

    data class BannerContext(
        val text: String,
        val textLines: List<String>,
        val width: Int,
        val height: Int,
    )

    private fun render() {
        val figFont = config.loadFigFont(config.fontName)
        val figletRenderer = FigletRenderer(figFont)
        val bannerText = figletRenderer.renderText(
            config.bannerText,
            config.smushMode ?: figFont.fullLayout,
            config.direction,
        )
        val bannerTextLines = bannerText.split("\n")
        val bannerHeight = bannerTextLines.size
        val bannerWidth = bannerTextLines.maxOf { it.length }
        val bannerContext = BannerContext(bannerText, bannerTextLines, bannerWidth, bannerHeight)

        config.before(bannerContext)
        config.render(bannerContext)
        config.after(bannerContext)
    }

    internal companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Banner> {
        override val key = AttributeKey<Banner>("Banner")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Banner {
            val configuration = Configuration().apply(configure)
            val feature = Banner(configuration)

            feature.render()

            return feature
        }
    }
}

@ContextDsl
fun Application.banner(configure: Banner.Configuration.() -> Unit) = install(Banner, configure)
