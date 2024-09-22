package com.example.voicechanger.utils

import org.json.JSONArray

fun JSONArray.toDoubleList(): List<Double> {
    val list = mutableListOf<Double>()
    for (i in 0 until this.length()) {
        list.add(this.getDouble(i))
    }
    return list
}

fun JSONArray.toIntList(): List<Int> {
    val list = mutableListOf<Int>()
    for (i in 0 until this.length()) {
        list.add(this.getInt(i))
    }
    return list
}