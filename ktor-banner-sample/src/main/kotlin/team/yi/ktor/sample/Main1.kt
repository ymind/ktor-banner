package team.yi.ktor.sample

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import team.yi.ktor.features.banner

fun main1() {
    embeddedServer(Netty, port = 8000) {
        banner {
            bannerText = "Ktor Banner"
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
