package com.sriyank.javatokotlindemo.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Repository(
        @PrimaryKey
        var id: Int? = 0,
        var name: String?= null,
        var language: String?= null,
        @SerializedName("html_url")
        var htmlUrl: String?= null,
        var description: String?= null,
        @SerializedName("stargazers_count")
        var stars: Int?= 0,
        @SerializedName("watchers_count")
        var watchers: Int?= 0,
        var forks: Int?= 0,
        var owner: Owner?= null
) : RealmObject()


open class Owner(var id: Int? =0 , var login: String? = null ): RealmObject()

open class SearchResponse(@SerializedName(
        "total_count")var total_count: Int? = null,
                          var items
                          : RealmList<Repository>? = null ) : RealmObject()

data class ErrorResponse (var message:String?)