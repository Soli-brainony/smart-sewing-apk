package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY dateCreated DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurement_profiles WHERE id = :id LIMIT 1")
    fun getProfileFlow(id: String = "default_user_profile"): Flow<MeasurementProfileEntity?>

    @Query("SELECT * FROM measurement_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "default_user_profile"): MeasurementProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: MeasurementProfileEntity)
}

@Database(entities = [ProjectEntity::class, MeasurementProfileEntity::class], version = 1, exportSchema = false)
abstract class SewSmartDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun measurementDao(): MeasurementDao
}
