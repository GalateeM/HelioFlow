package com.example.helioflow.placeholder

import java.util.ArrayList
import java.util.HashMap

enum class ShutterAction {
    OPEN, CLOSE
}

data class ShutterRule(
    val id: String,
    val action: ShutterAction,
    val hour: Int,
    val minute: Int,
    val days: Set<Int>
) {
    fun getDisplayTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }

    fun getDisplayDays(): String {
        val dayNames = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
        return if (days.isEmpty()) {
            "Aucun jour"
        } else if (setOf(5, 6).all { it in days }) {
            "Week-end"
        } else if (setOf(0, 1, 2, 3, 4).all { it in days }) {
            "Jours de semaine"
        } else if (days.size == 7) {
            "Tous les jours"
        } else {
            days.sorted().joinToString(", ") { dayNames[it] }
        }
    }

    fun getDisplayContent(): String {
        val actionText = if (action == ShutterAction.OPEN) "Ouverture" else "Fermeture"
        return "$actionText à ${this.getDisplayTime()}"
    }

    fun getDisplayDetails(): String {
        return getDisplayDays()
    }
}

object PlaceholderContent {

    val ITEMS: MutableList<ShutterRule> = ArrayList()
    val ITEM_MAP: MutableMap<String, ShutterRule> = HashMap()

    private fun addItem(item: ShutterRule) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }
}