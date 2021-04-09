@file:JvmName("MainCli")

import command.ConvertCommand

fun main(args: Array<String>) {
    if (args.isNullOrEmpty().not()) {
        val apk = args[0]
        val temp = args[1]
        val output = args[2]
        ConvertCommand(apk, temp, output).run()
    }
}
