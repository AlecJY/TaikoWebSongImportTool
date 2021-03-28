package com.alebit.taikoweb

import com.alebit.taikoweb.parser.parseGenre
import com.alebit.taikoweb.parser.parseTJA
import com.alebit.taikoweb.struct.Genre
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

fun importTJAs(source: Path, target: Path, taikoWeb: TaikoWeb, category: Genre = Genre.None, similarFile: Boolean = false) {
    val genreFile = source.resolve("genre.ini").toFile();
    var genre: Genre = category;
    if (source.resolve("genre.ini").toFile().exists()) {
        val newCategory = parseGenre(genreFile);
        if (newCategory != Genre.None) {
            genre = newCategory;
        }
    }
    val files = source.toFile().listFiles();
    for (file in files) {
        if (file.isDirectory) {
            importTJAs(file.toPath(), target, taikoWeb, genre, similarFile);
        } else if (file.extension.toLowerCase().equals("tja")) {
            println("Import TJA: " + file.absolutePath);
            val song = parseTJA(file, genre, similarFile);
            if (song.title == null || song.musicFile == null) {
                System.err.println("Could not parse TJA file \"" + file.absolutePath + "\"");
                continue;
            }
            taikoWeb.addSong(song, file, target);
        }
    }
}