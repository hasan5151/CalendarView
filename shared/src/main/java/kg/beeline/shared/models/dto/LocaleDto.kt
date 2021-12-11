package kg.beeline.shared.models.dto

import com.squareup.moshi.JsonClass
import kg.beeline.shared.models.enums.LocaleType

/** Created by Jahongir on 11/02/2021.*/

@JsonClass(generateAdapter = true)
data class LocaleDto(val language: LocaleType)