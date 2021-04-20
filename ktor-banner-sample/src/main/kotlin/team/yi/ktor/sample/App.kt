package team.yi.ktor.sample

fun main(args: Array<String>) {
    val index = if (args.isEmpty()) {
        1
    } else {
        args[0].toIntOrNull() ?: 1
    }

    when (index) {
        2 -> main2()
        3 -> main3()
        else -> main1()
    }
}
