package kg.beeline.shared.resource

import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kg.beeline.shared.models.bgDispatcher
import kg.beeline.shared.result.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ResultType: Type for the Resource data.
// RequestType: Type for the API response.
abstract class NetworkProcessResourceEvent<ResultType, RequestType>
@MainThread constructor(private val viewModelScope: CoroutineScope) {

    private val resultEvent = MediatorLiveData<Event<Resource<ResultType>>>()

    init {
        resultEvent.value = Event(Resource.loading(null))
        @Suppress("LeakingThis")
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (resultEvent.value?.peekContent() != newValue) {
            resultEvent.value = Event(newValue)
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        resultEvent.addSource(apiResponse) { response ->
            resultEvent.removeSource(apiResponse)
            Log.d("NetworkResource", "fetchFromNetwork: $response")
            when (response) {
                is ApiSuccessResponse -> {
                    viewModelScope.launch(bgDispatcher) {
                        val processedResponse = processResponse(response.body)
                        onCallSucceed(response.body, processedResponse)
                        withContext(viewModelScope.coroutineContext) {
                            setValue(Resource.success(processedResponse))
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    setValue(Resource.success(null))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage, null, code = response.errorCode))
                }
            }
        }
    }

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected open fun onFetchFailed() {}

    protected open suspend fun onCallSucceed(requestItem: RequestType, resultItem: ResultType) {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class
    fun asLiveData() = resultEvent as LiveData<Event<Resource<ResultType>>>

    @WorkerThread
    protected abstract fun processResponse(body: RequestType): ResultType

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}