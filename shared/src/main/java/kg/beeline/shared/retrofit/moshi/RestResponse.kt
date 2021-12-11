package kg.beeline.shared.retrofit.moshi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**Created by Jahongir on 8/5/2019.*/

@JsonClass(generateAdapter = true)
class RestResponse<T>(@field:Json(name = "status") var status: String? = null,
                      @field:Json(name = "message") var message: String? = null,
                      @field:Json(name = "data") val data: T)