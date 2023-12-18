package com.alebit.taikoweb.parser

fun String.trimBom(): String {
    val bom = "\uFEFF";
    if (this.startsWith(bom)) {
        return this.substring(1).trim()
    }
    return this.trim()
}