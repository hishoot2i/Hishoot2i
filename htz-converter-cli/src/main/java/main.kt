@file:JvmName("MainCli")

import command.ConvertCommand

fun main(args: Array<String>) {
    if (args.isNullOrEmpty().not()) {
        ConvertCommand(args[0]).run()
    }
}
