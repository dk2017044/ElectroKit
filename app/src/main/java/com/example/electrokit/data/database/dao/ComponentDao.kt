package com.example.electrokit.data.database.dao

import androidx.room.*
import com.example.electrokit.data.database.entity.ComponentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponentDao {

    @Query("SELECT * FROM components ORDER BY componentName ASC")
    fun getAllComponents(): Flow<List<ComponentEntity>>

    @Query("SELECT * FROM components WHERE partNumber = :partNumber LIMIT 1")
    suspend fun getComponentByPartNumber(partNumber: String): ComponentEntity?

    @Query("""
        SELECT * FROM components WHERE 
        partNumber LIKE '%' || :query || '%' OR
        componentName LIKE '%' || :query || '%' OR
        category LIKE '%' || :query || '%' OR
        keywordsRaw LIKE '%' || :query || '%' OR
        manufacturer LIKE '%' || :query || '%' OR
        equivalentComponentsRaw LIKE '%' || :query || '%' OR
        description LIKE '%' || :query || '%'
        ORDER BY componentName ASC
    """)
    fun searchComponents(query: String): Flow<List<ComponentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(components: List<ComponentEntity>)

    @Query("SELECT COUNT(*) FROM components")
    suspend fun getCount(): Int
}
