package com.alebit.taikoweb.parser

import com.alebit.taikoweb.struct.Genre
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun parseGenre(file: File, utf8: Boolean): Genre {
    val lines = file.readLines(if (utf8) StandardCharsets.UTF_8 else Charset.forName("Windows-31J"));
    for (line in lines) {
        val values = line.split(Regex.fromLiteral("="), 2);
        if (values.size == 2) {
            if (values[0].lowercase() == "genrename") {
                val genre = Genre.parse(values[1])
                if (genre != Genre.None) {
                    return genre
                }
            }
        }
    }
    return Genre.None;
}

fun parseBoxDef(file: File, utf8: Boolean): Genre {
    val lines = file.readLines(if (utf8) StandardCharsets.UTF_8 else Charset.forName("Windows-31J"));
    for (line in lines) {
        val values = line.split(Regex.fromLiteral(":"), 2);
        if (values.size == 2) {
            if (values[0].lowercase().startsWith("#title") || values[0].lowercase().startsWith("#genre")) {
                val genre = Genre.parse(values[1])
                if (genre != Genre.None) {
                    return genre
                }
            }
        }
    }
    return Genre.None;
}
