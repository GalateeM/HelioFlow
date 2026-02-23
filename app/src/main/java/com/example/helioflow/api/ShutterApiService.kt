package com.example.helioflow.api

import com.example.helioflow.placeholder.ShutterAction
import com.example.helioflow.placeholder.ShutterRule
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

data class Programmation(
    val id: Int = 0,
    val action: String = "",
    val days: String = "",
    val time: String = ""
)

data class CreateProgrammationRequest(
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

fun ShutterRule.toCreateRequest(): CreateProgrammationRequest {
    val actionStr = if (action == ShutterAction.OPEN) "open" else "close"
    
    val dayMap = mapOf(
        0 to "L",
        1 to "Ma",
        2 to "Me",
        3 to "J",
        4 to "V",
        5 to "S",
        6 to "D"
    )
    
    val daysStr = days.sorted().mapNotNull { dayMap[it] }.joinToString(", ")
    val timeStr = String.format("%02dh%02d", hour, minute)
    
    return CreateProgrammationRequest(
        action = actionStr,
        days = daysStr,
        time = timeStr
    )
}

interface ShutterApiService {

    @GET("programmations")
    suspend fun getProgrammationsRaw(): String

    @POST("programmations")
    suspend fun createProgrammation(@Body request: CreateProgrammationRequest): Programmation

    @PUT("programmations/{id}")
    suspend fun updateProgrammation(@Path("id") id: Int, @Body request: CreateProgrammationRequest): Programmation

    @DELETE("programmations/{id}")
    suspend fun deleteProgrammation(@Path("id") id: Int)

}

fun parseProgrammations(json: String): List<Programmation> {
    val trimmed = json.trim()
    if (trimmed.isEmpty() || trimmed == "{}" || trimmed == "[]") {
        return emptyList()
    }

    if (!trimmed.startsWith("[")) {
        return emptyList()
    }

    val result = mutableListOf<Programmation>()
    val itemRegex = Regex("""\{[^}]+\}""")
    val items = itemRegex.findAll(trimmed)

    for (item in items) {
        val obj = item.value
        val id = Regex(""""id":\s*(\d+)""").find(obj)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val action = Regex(""""action":\s*"([^"]+)"""").find(obj)?.groupValues?.get(1) ?: ""
        val days = Regex(""""days":\s*"([^"]+)"""").find(obj)?.groupValues?.get(1) ?: ""
        val time = Regex(""""time":\s*"([^"]+)"""").find(obj)?.groupValues?.get(1) ?: ""
        result.add(Programmation(id, action, days, time))
    }
    return result
}