package com.alebit.taikoweb

import com.alebit.taikoweb.struct.Genre
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import java.lang.Exception
import java.nio.file.Paths

fun main(args: Array<String>) {
    val argParser = ArgParser("TaikoWebSongImportTool");
    val category by argParser.option(ArgType.Choice<Genre>(), "category", "c", "The default category of songs. It would be overwritten if genre.ini is existed.");
    val similar by argParser.option(ArgType.Boolean, "similar-file", "s", "Find the most similar file under the directory of the TJA if the file is not found. This could prevent problem caused by encoding or case sensitivity.");
    val utf8 by argParser.option(ArgType.Boolean, "utf-8", "u", "If this option is chosen, the parser will use UTF-8 instead of Windows-31J.");
    val source by argParser.argument(ArgType.String, "Source", "The path to the directory of TJA files. It would find TJAs recursively.");
    val dest by argParser.argument(ArgType.String, "Destination", "The path to taiko-web songs directory");
    val url by argParser.argument(ArgType.String, "URL", "taiko-web URL");
    val user by argParser.argument(ArgType.String, "Username", "taiko-web admin username");
    val passwd by argParser.argument(ArgType.String, "Password", "taiko-web admin password");
    argParser.parse(args);
    try {
        val taikoWeb = TaikoWeb(url, user, passwd);
        val songs = loadTJAs(Paths.get(source), category ?: Genre.None, similar ?: false, utf8 ?: false);
        for (song in songs) {
            println("Import: " + song.chartFile.absolutePath);
            taikoWeb.addSong(song, Paths.get(dest));
        }
    } catch (e: Exception) {e.printStackTrace()}
}
