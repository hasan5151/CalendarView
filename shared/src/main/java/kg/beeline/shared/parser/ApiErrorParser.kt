package kg.beeline.shared.parser

import org.json.JSONArray

/** Created by Jahongir on 03/03/2021.*/
object ApiErrorParser {

    fun parseError(error: String?): String? {
        if (error.isNullOrBlank()) return error
        var errorMsg = parseErrorNestedJson(error)
        if (errorMsg == null) {
            errorMsg = XmlUtil.parseErrorMessage(error)
        }
        return errorMsg
    }

    private fun parseErrorNestedJson(error: String?): String? {
        if (error.isNullOrBlank()) return null
        val startPosition = error.indexOf(':')
        if (startPosition == -1) return null
        val jaRaw = error.subSequence(startPosition + 1, error.length).toString()
        val jaStr = jaRaw.replace("""\r\n""", "").replace("""\""", "")
        return try {
            val ja = JSONArray(jaStr)
            if (ja.length() == 1) {
                val jo = ja.getJSONObject(0)
                jo.getString("message")
            } else {
                error
            }
        } catch (ex: Exception) {
            error
        }
    }
}