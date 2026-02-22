package com.example.helioflow.api

import com.example.helioflow.placeholder.ShutterAction
import com.example.helioflow.placeholder.ShutterRule
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

data class Programmation(
    val id: Int,
    val action: String,
    val days: String,
    val time: String
)

fun Programmation.toShutterRule(): ShutterRule {
    val actionEnum = if (action.lowercase() == "open") ShutterAction.OPEN else ShutterAction.CLOSE
    
    val dayMap = mapOf(
        "L" to 0,
        "Ma" to 1,
        "Me" to 2,
        "J" to 3,
        "V" to 4,
        "S" to 5,
        "D" to 6
    )
    
    val daysSet = days.split(",")
        .map { it.trim() }
        .mapNotNull { dayMap[it] }
        .toSet()
    
    val timeRegex = Regex("(\\d+)h(\\d+)")
    val matchResult = timeRegex.find(time)
    val hour = matchResult?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val minute = matchResult?.groupValues?.get(2)?.toIntOrNull() ?: 0
    
    return ShutterRule(
        id = id.toString(),
        action = actionEnum,
        hour = hour,
        minute = minute,
        days = daysSet
    )
}

interface ShutterApiService {

    @GET("programmations")
    suspend fun getProgrammations(): List<Programmation>

}