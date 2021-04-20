package team.yi.ktor.sample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team.yi.kfiglet.FigFont
import team.yi.ktor.features.banner

fun main2() {
    embeddedServer(Netty, port = 8000) {
        banner {
            bannerText = "Ktor Banner"

            // custom font
            loadFigFont = {
                val inputStream = ::main2.javaClass.classLoader.getResourceAsStream("fonts/starwars.flf")!!

                FigFont.loadFigFont(inputStream)
            }

            // custom render
            render {
                println(it.text)
            }

            // append custom footer
            beforeBanner { banner ->
                println("".padEnd(banner.width, '-'))
            }

            // append custom footer
            afterBanner { banner ->
                val title = " MyKtorApp v1.2.3 "
                val homepage = "https://yi.team/"
                val filling = "".padEnd(banner.width - title.length - homepage.length, ' ')

                println("".padEnd(banner.width, '-'))
                println("$title$filling$homepage")
            }
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
