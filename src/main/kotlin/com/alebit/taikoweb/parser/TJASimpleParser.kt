package com.alebit.taikoweb.parser

import com.alebit.taikoweb.struct.Genre
import com.alebit.taikoweb.struct.TaikoWebSong
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.nio.charset.Charset
import java.nio.file.Path

private val similarity = JaroWinklerSimilarity();

fun parseTJA(file: File, category: Genre, similarFile: Boolean): TaikoWebSong {
    val lines = file.readLines(Charset.forName("Windows-31J"));
    val song = TaikoWebSong(file);
    var course = 3;
    var level = 10;
    song.courseOni = level;
    song.category = category;
    song.chartType = "tja";
    for (line in lines) {
        val values = line.split(Regex.fromLiteral(":"), 2);
        if (values.size == 2) {
            val value = values[1].trim();
            when (values[0].toUpperCase()) {
                "TITLE" -> song.title = value;
                "TITLEJA" -> song.titleJa = value;
                "TITLEEN" -> song.titleEn = value;
                "TITLECN" -> song.titleCn = value;
                "TITLETW" -> song.titleTw = value;
                "TITLEKO" -> song.titleKo = value;
                "SUBTITLE" -> song.subtitle = trimSubtitle(value);
                "SUBTITLEJA" -> song.subtitleJa = trimSubtitle(value);
                "SUBTITLEEN" -> song.subtitleEn = trimSubtitle(value);
                "SUBTITLECN" -> song.subtitleCn = trimSubtitle(value);
                "SUBTITLETW" -> song.subtitleTw = trimSubtitle(value);
                "SUBTITLEKO" -> song.subtitleKo = trimSubtitle(value);
                "WAVE" -> {
                    try {
                        val wave = similarFile(file.toPath().parent, value, similarFile);
                        if (!wave.exists()) {
                            val fallbackOggFile = File(file.toPath().parent.toString(), "main.ogg")
                            val fallbackMp3File = File(file.toPath().parent.toString(), "main.mp3")
                            if (fallbackOggFile.exists()) {
                                song.musicType = "ogg";
                                song.musicFile = fallbackOggFile;
                            } else if (fallbackMp3File.exists()) {
                                song.musicType = "mp3";
                                song.musicFile = fallbackMp3File;
                            } else {
                                System.err.println("Could not find music \"" + wave.absolutePath + "\"");
                                break;
                            }
                        } else if (wave.extension.toLowerCase().equals("ogg")) {
                            song.musicType = "ogg";
                            song.musicFile = wave;
                        } else if (wave.extension.toLowerCase().equals("mp3")) {
                            song.musicType = "mp3";
                            song.musicFile = wave;
                        } else {
                            System.err.println("Unsupport music format \"" + wave.absolutePath + "\"");
                            break;
                        }
                    } catch (e: Exception) {
                        System.err.println("Unknown error when parse \"" + file.absolutePath + "\"");
                        break;
                    }
                }
                "DEMOSTART" -> {
                    try {
                        if (value.isEmpty()) {
                            song.preview = 0.0;
                        } else {
                            song.preview = value.toDouble();
                        }
                    } catch (e: NumberFormatException) {
                        System.err.println("Cannot parse DEMOSTART value \"" + value + "\"");
                        break;
                    }
                }
                "LYRICS" -> {
                    try {
                        val lyric = similarFile(file.toPath().parent, value, similarFile);
                        if (!lyric.exists()) {
                            System.err.println("Could not find lyric \"" + lyric.absolutePath + "\"");
                            break;
                        } else if (lyric.extension.toLowerCase().equals("vtt")) {
                            song.lyrics = true;
                            song.lyricFile = lyric;
                        } else {
                            System.err.println("Unsupport lyric format \"" + lyric.absolutePath + "\"");
                            break;
                        }
                    } catch (e: Exception) {
                        System.err.println("Unknown error when parse \"" + file.absolutePath + "\"");
                        break;
                    }
                }
                "SONGVOL" -> {
                    try {
                        song.volume = value.toInt() / 100.0;
                    } catch (e: NumberFormatException) {
                        System.err.println("Could parse SONGVOL value \"" + value + "\"");
                        break;
                    }
                }
                "COURSE" -> {
                    when(value.toLowerCase()) {
                        "0", "easy" -> {course = 0; song.courseEasy = level;}
                        "1", "normal" -> {course = 1; song.courseNormal = level;}
                        "2", "hard" -> {course = 2; song.courseHard = level;}
                        "3", "oni" -> {course = 3; song.courseOni = level;}
                        "4", "edit", "ura" -> {course = 4; song.courseUra = level;}
                        else -> {
                            println(file.path)
                            println("Ignore unknown course \"" + value + "\"");
                        };
                    }
                }
                "LEVEL" -> {
                    try {
                        level = value.toInt();
                    } catch (e: NumberFormatException) {
                        System.err.println("Cannot parse LEVEL value \"" + value + "\"");
                        level = 10;
                    }
                    when (course) {
                        0 -> song.courseEasy = level;
                        1 -> song.courseNormal = level;
                        2 -> song.courseHard = level;
                        3 -> song.courseOni = level;
                        4 -> song.courseUra = level;
                    }
                }
            }
        }
        if (line.startsWith("#BRANCHSTART")) {
            when (course) {
                0 -> song.branchEasy = true;
                1 -> song.branchNormal = true;
                2 -> song.branchHard = true;
                3 -> song.branchOni = true;
                4 -> song.branchUra = true;
            }
        }
    }
    return song;
}

private fun similarFile(path: Path, filename: String, similarFile: Boolean): File {
    val orig = path.resolve(filename).toFile();
    if (similarFile) {
        if (orig.exists()) {
            return orig;
        }
        val origExt = orig.extension.toLowerCase();
        var sim: Double = Double.MIN_VALUE;
        var simFile = orig;
        for (file in path.toFile().listFiles()) {
            if (file.extension.toLowerCase().equals(origExt)) {
                val s = similarity.apply(orig.nameWithoutExtension, file.nameWithoutExtension);
                if (s > sim) {
                    sim = s;
                    simFile = file;
                }
            }
        }
        return simFile;
    } else {
        return orig;
    }
}

private fun trimSubtitle(subtitle: String): String {
    if (subtitle.startsWith("--") || subtitle.startsWith("++")) {
        return subtitle.substring(2);
    }
    return subtitle;
}