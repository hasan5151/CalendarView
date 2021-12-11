package kg.beeline.shared.retrofit

import java.net.ConnectException

/**Created by Jahongir on 9/27/2019.*/

class NoInternetException(msg: String, override val cause: Throwable?) : ConnectException(msg)

class ServerDownException(msg: String, override val cause: Throwable?) : ConnectException(msg)