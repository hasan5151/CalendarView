package kg.beeline.shared.extensions

import android.util.Base64
import java.io.File
import java.util.*

/** Created by Jahongir on 21/12/2020.*/
val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024

val String.sizeInKb get():Double = (Base64.decode(this, Base64.NO_WRAP).size / 1024).toDouble()

val Double.sizeInMb get() = this / 1024

val Double.formatSize get() = "%.1f".format(Locale.US,this)