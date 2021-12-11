package kg.beeline.shared.parser

import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

object XmlUtil {

    private val xmlParser: XmlPullParser by lazy { buildXmpParser() }

    fun parseErrorMessage(message: String?): String? {
        return try {
            if (message.isNullOrBlank()) return null
            if ("RestResponse" !in message) return null
            parseXml(message)
        } catch (ex: Exception) {
            Log.e("XmlUtil", "parseErrorMessage: ", ex)
            null
        }
    }

    private fun fetchXmlResponse(input: String?): String? {
        if (input == null) return null
        return if ("[" in input && "]" in input) {
            input.subSequence(input.indexOf("[") + 1, input.indexOf("]")).toString()
        } else input
    }

    @Suppress("SameParameterValue")
    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseXml(errorMessage: String?): String? {
        val clearMsg = fetchXmlResponse(errorMessage)
        xmlParser.setInput(clearMsg?.byteInputStream(), null)
        xmlParser.nextTag()
        xmlParser.require(XmlPullParser.START_TAG, null, "RestResponse")
        var code: String? = null
        var data: String? = null
        var type: String? = null
        while (xmlParser.next() != XmlPullParser.END_TAG) {
            //If its not document start skip
            if (xmlParser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (xmlParser.name) {
                "code" -> {
                    code = readTextSafe("code")
                }
                "data" -> {
                    data = parseDataIfSimple()
                }
                "type" -> {
                    type = readTextSafe("type")
                }
                else -> skip(xmlParser)
            }
        }
        Log.d("XmlUtil", "parseXml: code:$code data:$data type:$type")
        var result: String? = null
        if (data != null)
            result = result?.let { "$it data:$data" } ?: data
        if (type != null)
            result = result?.let { "$it ex:$type" } ?: type
        return result
    }

    private fun parseDataIfSimple(): String? {
        if (xmlParser.next() == XmlPullParser.TEXT) {
            val result = xmlParser.text
            xmlParser.nextTag()
            xmlParser.require(XmlPullParser.END_TAG, null, "data")
            return result
        }
        var isFinished = false
        while (!isFinished) {
            xmlParser.next()
            isFinished = xmlParser.eventType == XmlPullParser.END_TAG && xmlParser.name == "data"
        }
        return null
    }

    private fun readTextSafe(tag: String): String? {
        return try {
            xmlParser.require(XmlPullParser.START_TAG, null, tag)
            var result: String? = null
            if (xmlParser.next() == XmlPullParser.TEXT) {
                result = xmlParser.text
                xmlParser.nextTag()
                xmlParser.require(XmlPullParser.END_TAG, null, tag)
            }
            return result
        } catch (ex: XmlPullParserException) {
            Log.e("XmlUtil", "readTextSafe Exception:", ex)
            null
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun buildXmpParser(): XmlPullParser {
        val parser: XmlPullParser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        return parser
    }
}