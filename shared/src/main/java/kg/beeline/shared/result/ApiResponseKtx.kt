package kg.beeline.shared.result

import androidx.annotation.StringRes

/** Created by Jahongir on 19/12/2020.*/
inline fun <reified T> ApiResponse<T>.asResource(errorData: T? = null, @StringRes errorMsgRes: Int? = null): Resource<T> {
    return when (this) {
        is ApiSuccessResponse -> Resource.success(data = this.body)
        is ApiErrorResponse -> Resource.error(msg = this.errorMessage, data = errorData, code = this.errorCode, messageRes = errorMsgRes)
        is ApiEmptyResponse -> Resource.error("Empty response body")
    }
}

inline fun <reified T> ApiResponse<*>.castResponse(apiName: String? = null): Resource<T> {
    return when (this) {
        is ApiErrorResponse -> Resource.error(msg = this.errorMessage, code = this.errorCode)
        is ApiEmptyResponse -> Resource.error("Empty body response from: $apiName")
        is ApiSuccessResponse -> Resource.success(null)
    }
}

fun ApiResponse<*>.toResponse(apiName: String? = null): Resource<Nothing> {
    return when (this) {
        is ApiEmptyResponse -> Resource.error("Empty body response from: $apiName")
        is ApiErrorResponse -> Resource.error(msg = this.errorMessage, code = this.errorCode)
        is ApiSuccessResponse -> Resource.success(null)
    }
}

val ApiResponse<*>.isFailed: Boolean
    get() {
        return when (this) {
            is ApiErrorResponse, is ApiEmptyResponse -> true
            is ApiSuccessResponse -> false
        }
    }
