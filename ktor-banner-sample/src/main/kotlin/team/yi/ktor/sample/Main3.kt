package team.yi.ktor.sample

import com.diogonunes.jcolor.Ansi
import com.diogonunes.jcolor.Attribute
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team.yi.kfiglet.FigFont
import team.yi.ktor.features.banner

fun main3() {
    embeddedServer(Netty, port = 8000) {
        banner {
            bannerText = "star wars"

            // OPTIONAL: how to load custom font
            loadFigFont = {
                val inputStream = ::main3.javaClass.classLoader.getResourceAsStream("fonts/starwars.flf")!!

                FigFont.loadFigFont(inputStream)
            }

            // color it with: https://github.com/dialex/JColor
            render {
                println(Ansi.colorize(it.text, Attribute.GREEN_TEXT()))
            }

            // append custom footer
            afterBanner { banner ->
                val title = " MyKtorApp v1.2.3 "
                val homepage = "https://yi.team/"
                val filling = "".padEnd(banner.width - title.length - homepage.length, ' ')

                println(Ansi.colorize("".padEnd(banner.width, '-'), Attribute.WHITE_TEXT()))
                println(
                    Ansi.colorize(title, Attribute.BRIGHT_WHITE_TEXT(), Attribute.BOLD(), Attribute.MAGENTA_BACK()) +
                        filling +
                        homepage
                )

                for (i in 0 until banner.width) {
                    val colorValue = 1 + i % 14

                    print(Ansi.colorize("*", Attribute.TEXT_COLOR(colorValue), Attribute.STRIKETHROUGH()))
                }

                println()
            }
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
