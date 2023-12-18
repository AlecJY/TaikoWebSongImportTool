package com.alebit.taikoweb.struct

enum class Genre(val value: Int) {
    None(0),
    Pop(1),
    Anime(2),
    Vocaloid(3),
    Variety(4),
    Classical(5),
    Game(6),
    Namco(7);

    companion object {
        fun parse(value: String): Genre {
            when(value.trim().lowercase()) {
                "j-pop", "pop", "流行音乐", "流行音樂" -> return Pop;
                "アニメ", "anime", "卡通动画音乐", "卡通動畫音樂", "애니메이션" -> return Anime;
                "ボーカロイド™曲", "vocaloid™ music", "ボーカロイド曲", "ボーカロイド", "vocaloid music", "vocaloid" -> return Vocaloid;
                "バラエティ", "variety", "综合音乐", "綜合音樂", "버라이어티", "バラエティー", "どうよう", "童謡・民謡", "children", "children/folk", "children-folk" -> return Variety;
                "クラシック", "classical", "古典音乐", "古典音樂", "클래식", "クラッシック", "classic" -> return Classical;
                "ゲームミュージック", "game music", "游戏音乐", "遊戲音樂", "게임" -> return Genre.Game;
                "ナムコオリジナル", "namco original", "namco原创音乐", "namco原創音樂", "남코 오리지널" -> return Namco
                else -> return None
            }
        }
    }
}