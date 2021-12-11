package kg.beeline.shared.models

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
/**Created by Jahongir on 1/27/2020.*/

val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
val bgDispatcher: CoroutineDispatcher = Dispatchers.IO
val ioDispatcher: CoroutineDispatcher = Dispatchers.IO