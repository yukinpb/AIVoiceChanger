package com.example.voicechanger.module

import com.example.voicechanger.model.AudioAttrModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ParsingJsonObjects {
    companion object {
        fun jsonToObjectEffects(str: String?): AudioAttrModel? {
            if (str.isNullOrEmpty()) {
                return null
            }
            try {
                val jSONObject = JSONObject(str)
                val id = jSONObject.getString("id")
                val name = jSONObject.getString("name")
                val pitch = jSONObject.getInt("pitch")
                val rate = jSONObject.getInt("rate")
                val isReverse = jSONObject.optBoolean("reverse", false)
                val audioAttr = AudioAttrModel(id, name, pitch, rate)
                audioAttr.isReverse = isReverse

                jSONObject.optDouble("amplify").takeIf { it != 0.0 }?.let { audioAttr.amplify = it.toFloat() }
                jSONObject.optDouble("rotate").takeIf { it != 0.0 }?.let { audioAttr.rotate = it.toFloat() }
                jSONObject.optJSONArray("reverb")?.let { audioAttr.reverb = jsonArrayToList(it) }
                jSONObject.optJSONArray("distort")?.let { audioAttr.distort = jsonArrayToList(it) }
                jSONObject.optJSONArray("chorus")?.let { audioAttr.chorus = jsonArrayToList(it) }
                jSONObject.optJSONArray("flanger")?.let { audioAttr.flanger = jsonArrayToList(it) }
                jSONObject.optJSONArray("filter")?.let { audioAttr.filter = jsonArrayToList(it) }
                jSONObject.optJSONArray("echo")?.let { audioAttr.echo = jsonArrayToList(it) }
                jSONObject.optJSONArray("echo4")?.let { audioAttr.echo4 = jsonArrayToList(it) }
                jSONObject.optJSONArray("eq1")?.let { audioAttr.echo1 = jsonArrayToList(it) }
                jSONObject.optJSONArray("eq2")?.let { audioAttr.eq2 = jsonArrayToList(it) }
                jSONObject.optJSONArray("eq3")?.let { audioAttr.eq3 = jsonArrayToList(it) }
                jSONObject.optJSONArray("phaser")?.let { audioAttr.phaser = jsonArrayToList(it) }
                jSONObject.optJSONArray("autowah")?.let { audioAttr.autoWah = jsonArrayToList(it) }
                jSONObject.optJSONArray("compressor")?.let { audioAttr.compressor = jsonArrayToList(it) }

                return audioAttr
            } catch (e: JSONException) {
                e.printStackTrace()
                return null
            }
        }

        private fun jsonArrayToList(jsonArray: JSONArray): List<Float> {
            val list = mutableListOf<Float>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getDouble(i).toFloat())
            }
            return list
        }
    }
}