package com.example.data.db

import android.content.Context
import androidx.room.Room
import com.example.data.model.BodyMeasurements
import com.example.data.model.UnitSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(context: Context) {
    private val database: SewSmartDatabase = Room.databaseBuilder(
        context.applicationContext,
        SewSmartDatabase::class.java,
        "sewsmart_db"
    ).fallbackToDestructiveMigration().build()

    private val projectDao = database.projectDao()
    private val measurementDao = database.measurementDao()

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    val userMeasurementsFlow: Flow<BodyMeasurements> = measurementDao.getProfileFlow()
        .map { entity ->
            if (entity != null) {
                BodyMeasurements(
                    unitSystem = if (entity.unitSystem == "CENTIMETERS") UnitSystem.CENTIMETERS else UnitSystem.INCHES,
                    bust = entity.bust,
                    waist = entity.waist,
                    hip = entity.hip,
                    shoulderWidth = entity.shoulderWidth,
                    neckCircumference = entity.neckCircumference,
                    bustPoint = entity.bustPoint,
                    bustHeight = entity.bustHeight,
                    shoulderToWaist = entity.shoulderToWaist,
                    backWaistLength = entity.backWaistLength,
                    armholeDepth = entity.armholeDepth,
                    upperArmCircumference = entity.upperArmCircumference,
                    sleeveLength = entity.sleeveLength,
                    wristCircumference = entity.wristCircumference,
                    desiredGarmentLength = entity.desiredGarmentLength
                )
            } else {
                BodyMeasurements() // default measurements
            }
        }

    suspend fun saveMeasurements(m: BodyMeasurements, name: String = "My Measurements") {
        val entity = MeasurementProfileEntity(
            id = "default_user_profile",
            profileName = name,
            lastUpdated = System.currentTimeMillis(),
            unitSystem = m.unitSystem.name,
            bust = m.bust,
            waist = m.waist,
            hip = m.hip,
            shoulderWidth = m.shoulderWidth,
            neckCircumference = m.neckCircumference,
            bustPoint = m.bustPoint,
            bustHeight = m.bustHeight,
            shoulderToWaist = m.shoulderToWaist,
            backWaistLength = m.backWaistLength,
            armholeDepth = m.armholeDepth,
            upperArmCircumference = m.upperArmCircumference,
            sleeveLength = m.sleeveLength,
            wristCircumference = m.wristCircumference,
            desiredGarmentLength = m.desiredGarmentLength
        )
        measurementDao.insertOrUpdateProfile(entity)
    }

    suspend fun saveProject(project: ProjectEntity): Long {
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project)
    }

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
    }

    suspend fun getProjectById(id: Long): ProjectEntity? {
        return projectDao.getProjectById(id)
    }
}
