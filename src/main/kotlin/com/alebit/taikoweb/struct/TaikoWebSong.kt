package com.alebit.taikoweb.struct

import java.io.File

data class TaikoWebSong (val chartFile: File): Comparable<TaikoWebSong> {
    var title: String? = null;
    var titleJa: String? = null;
    var titleEn: String? = null;
    var titleCn: String? = null;
    var titleTw: String? = null;
    var titleKo: String? = null;
    var subtitle: String? = null;
    var subtitleJa: String? = null;
    var subtitleEn: String? = null;
    var subtitleCn: String? = null;
    var subtitleTw: String? = null;
    var subtitleKo: String? = null;
    var courseEasy: Int? = null;
    var branchEasy: Boolean = false;
    var courseNormal: Int? = null;
    var branchNormal: Boolean = false;
    var courseHard: Int? = null;
    var branchHard: Boolean = false;
    var courseOni: Int? = null;
    var branchOni: Boolean = false;
    var courseUra: Int? = null;
    var branchUra: Boolean = false;
    var category: Genre = Genre.None;
    var chartType: String = "tja";
    var musicType: String = "ogg";
    var preview: Double = 0.0;
    var volume: Double = 1.0;
    var lyrics: Boolean = false;
    var musicFile: File? = null;
    var lyricFile: File? = null;

    override fun compareTo(other: TaikoWebSong): Int {
        return this.title!!.compareTo(other.title!!);
    }
}
