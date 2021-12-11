package kg.beeline.shared.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

/**Created by Jahongir on 8/15/2019.*/

/**
 * A LiveData class that has `null` value.
 */
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {
    init {
        // use post instead of set since this can be created on any thread
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }

        fun <T> createEmptyList(): LiveData<List<T>> {
            return liveData {
                emit(emptyList<T>())
            }
        }

        fun <T> createResourceEmptyList(): LiveData<Resource<List<T>>> {
            return liveData {
                emit(Resource.error("Empty result", emptyList<T>()))
            }
        }

        fun <T> createResource(): LiveData<Resource<T>> {
            return liveData {
                emit(Resource.error("Empty result", null) as Resource<T>)
            }
        }
    }
}
