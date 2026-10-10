package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val garmentType: String,
    val dateCreated: Long = System.currentTimeMillis(),
    val notes: String = "",
    val testGarmentAdjustments: String = "",
    val currentVersion: Int = 1,
    val analysisSummary: String = "",
    val difficulty: String = "Intermediate",
    val estimatedHours: String = "4 – 6 hours",
    val measurementsJson: String = "",
    val easeInches: Float = 1.5f,
    val seamAllowanceInches: Float = 0.5f,
    val fabricYards: Float = 3.0f,
    val thumbnailBase64: String = ""
)

@Entity(tableName = "measurement_profiles")
data class MeasurementProfileEntity(
    @PrimaryKey val id: String = "default_user_profile",
    val profileName: String = "My Measurements",
    val lastUpdated: Long = System.currentTimeMillis(),
    val unitSystem: String = "INCHES",
    val bust: Float = 36f,
    val waist: Float = 28f,
    val hip: Float = 39f,
    val shoulderWidth: Float = 15f,
    val neckCircumference: Float = 14f,
    val bustPoint: Float = 10f,
    val bustHeight: Float = 10f,
    val shoulderToWaist: Float = 16f,
    val backWaistLength: Float = 15.5f,
    val armholeDepth: Float = 7.5f,
    val upperArmCircumference: Float = 11.5f,
    val sleeveLength: Float = 22f,
    val wristCircumference: Float = 6.5f,
    val desiredGarmentLength: Float = 40f
)
