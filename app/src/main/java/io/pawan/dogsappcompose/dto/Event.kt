package io.pawan.dogsappcompose.dto

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.annotation.Keep

@Keep
@Parcelize
data class Event(
    @SerializedName("branch_key")
    val branchKey: String? = null,
    @SerializedName("custom_data")
    val customData: CustomData? = null,
    @SerializedName("customer_event_alias")
    val customerEventAlias: String? = null,
    @SerializedName("metadata")
    val metadata: Metadata? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("user_data")
    val userData: UserData?
) : Parcelable

@Keep
@Parcelize
data class CustomData(
    @SerializedName("breed")
    val breed: String? = null
) : Parcelable

@Keep
@Parcelize
class Metadata : Parcelable

@Keep
@Parcelize
data class UserData(
    @SerializedName("aaid")
    val aaid: String? = null,
    @SerializedName("advertising_ids")
    val advertisingIds: AdvertisingIds? = null,
    @SerializedName("android_id")
    val androidId: String? = null,
    @SerializedName("app_version")
    val appVersion: String? = null,
    @SerializedName("brand")
    val brand: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("developer_identity")
    val developerIdentity: String? = null,
    @SerializedName("environment")
    val environment: String? = null,
    @SerializedName("ip")
    val ip: String? = null,
    @SerializedName("language")
    val language: String? = null,
    @SerializedName("limit_ad_tracking")
    val limitAdTracking: Boolean? = null,
    @SerializedName("local_ip")
    val localIp: String? = null,
    @SerializedName("model")
    val model: String? = null,
    @SerializedName("os")
    val os: String? = null,
    @SerializedName("os_version")
    val osVersion: Int? = null,
    @SerializedName("screen_dpi")
    val screenDpi: Int? = null,
    @SerializedName("screen_height")
    val screenHeight: Int? = null,
    @SerializedName("screen_width")
    val screenWidth: Int? = null
) : Parcelable

@Keep
@Parcelize
data class AdvertisingIds(
    @SerializedName("oaid")
    val oaid: String? = null
) : Parcelable