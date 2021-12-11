package kg.beeline.shared.retrofit

import kg.beeline.shared.result.ApiResponse
import retrofit2.Response

/**Created by Jahongir on 10/9/2019.*/

suspend fun <T> suspendApiCall(apiService: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        ApiResponse.create(apiService.invoke())
    } catch (ex: Exception) {
        ApiResponse.create(ex)
    }
}