package com.alebit.taikoweb.parser

import com.alebit.taikoweb.struct.Genre
import java.io.File
import java.nio.charset.Charset

fun parseGenre(file: File): Genre {
    val lines = file.readLines(Charset.forName("Windows-31J"));
    for (line in lines) {
        val values = line.split(Regex.fromLiteral("="), 2);
        if (values.size == 2) {
            if (values[0].toLowerCase().equals("genrename")) {
                when (values[1].toLowerCase()) {
                    "j-pop", "pop", "流行音乐", "流行音樂" -> return Genre.Pop;
                    "アニメ", "anime", "卡通动画音乐", "卡通動畫音樂", "애니메이션" -> return Genre.Anime;
                    "ボーカロイド™曲", "vocaloid™ music", "ボーカロイド曲", "ボーカロイド", "vocaloid music", "vocaloid" -> return Genre.Vocaloid;
                    "バラエティ", "variety", "综合音乐", "綜合音樂", "버라이어티", "バラエティー", "どうよう", "童謡・民謡", "children", "children/folk", "children-folk" -> return  Genre.Variety;
                    "クラシック", "classical", "古典音乐", "古典音樂", "클래식", "クラッシック", "classic" -> return Genre.Classical;
                    "ゲームミュージック", "game Music", "游戏音乐", "遊戲音樂", "게임" -> return Genre.Game;
                    "ナムコオリジナル", "namco original", "namco原创音乐", "namco原創音樂", "남코 오리지널" -> return Genre.Namco
                }
            }
        }
    }
    return Genre.None;
}