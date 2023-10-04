package io.pawan.dogsappcompose.dto

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
@Parcelize
data class Breed(
    @SerializedName("message")
    val message: List<String?> = emptyList(),
    @SerializedName("status")
    val status: String = ""
) : Parcelable