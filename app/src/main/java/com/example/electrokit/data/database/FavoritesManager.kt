package com.example.electrokit.data.database

import android.content.Context
import android.content.SharedPreferences

object FavoritesManager {
    private const val PREFS_NAME = "electrokit_favorites"
    private const val KEY_FAVORITES = "favorite_part_numbers"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getFavorites(context: Context): Set<String> {
        return getPrefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(context: Context, partNumber: String): Boolean {
        val prefs = getPrefs(context)
        val current = prefs.getStringSet(KEY_FAVORITES, emptySet())?.toMutableSet() ?: mutableSetOf()
        val isFav = if (current.contains(partNumber)) {
            current.remove(partNumber)
            false
        } else {
            current.add(partNumber)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return isFav
    }

    fun isFavorite(context: Context, partNumber: String): Boolean {
        return getFavorites(context).contains(partNumber)
    }
}
