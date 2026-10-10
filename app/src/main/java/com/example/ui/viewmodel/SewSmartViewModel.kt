package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiGarmentAnalyzer
import com.example.data.db.ProjectEntity
import com.example.data.db.ProjectRepository
import com.example.data.model.*
import com.example.data.pattern.FabricEstimationResult
import com.example.data.pattern.PatternEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    UPLOAD_STEP_1,
    ANALYSIS_RESULT,
    MEASUREMENTS_STEP_2,
    PATTERN_STEP_3,
    CUTTING_PLAN_STEP_4,
    SEWING_GUIDE_STEP_5,
    PRINTABLE_PATTERN,
    PROJECTS_LIST,
    DICTIONARY,
    FABRIC_GUIDE
}

class SewSmartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProjectRepository(application)

    val savedProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation Stack
    private val _screenStack = MutableStateFlow(listOf(AppScreen.HOME))
    val screenStack: StateFlow<List<AppScreen>> = _screenStack.asStateFlow()
    val currentScreen: AppScreen get() = _screenStack.value.lastOrNull() ?: AppScreen.HOME

    // Upload & Analysis State
    val selectedGarmentType = MutableStateFlow(GarmentType.AUTO_IDENTIFY)
    val uploadedBitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val userNotes = MutableStateFlow("")
    val isAnalyzing = MutableStateFlow(false)
    val analysisError = MutableStateFlow<String?>(null)
    val currentAnalysis = MutableStateFlow<GarmentAnalysis?>(null)

    // Measurements
    val currentMeasurements = MutableStateFlow(BodyMeasurements())
    val measurementUnit = MutableStateFlow(UnitSystem.INCHES)

    // Pattern State
    val currentPieces = MutableStateFlow<List<PatternPiece>>(emptyList())
    val calculationBreakdowns = MutableStateFlow<List<PatternCalculationBreakdown>>(emptyList())
    val selectedPieceIndex = MutableStateFlow(0)
    val customEaseInches = MutableStateFlow(1.5f)
    val customSeamAllowanceInches = MutableStateFlow(0.5f)
    val customGarmentLengthInches = MutableStateFlow<Float?>(null)
    val patternVersion = MutableStateFlow(1)
    val patternUpdatedNotice = MutableStateFlow(false)

    // Fabric Calculator
    val fabricWidthInches = MutableStateFlow(45) // 45" (Ankara) or 60"
    val matchPrints = MutableStateFlow(false)
    val fabricEstimation = MutableStateFlow<FabricEstimationResult?>(null)

    // Test Garment Adjustments
    val testGarmentLog = MutableStateFlow<List<String>>(emptyList())
    val testGarmentInput = MutableStateFlow("")

    // Ask SewSmart AI Teacher
    val isTeacherSheetOpen = MutableStateFlow(false)
    val teacherMessages = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "teacher" to "Hello! I'm SewSmart AI, your fashion design & sewing instructor. Ask me anything about garment construction, setting sleeves, sewing darts, sewing with Ankara wax prints, or adjusting patterns!"
        )
    )
    val isTeacherThinking = MutableStateFlow(false)

    // Active Project
    val activeProjectId = MutableStateFlow<Long?>(null)
    val activeProjectName = MutableStateFlow("New Garment Project")
    val projectSavedNotice = MutableStateFlow(false)

    init {
        // Load initial measurements from local Room storage
        viewModelScope.launch {
            repository.userMeasurementsFlow.collect { saved ->
                currentMeasurements.value = saved
                measurementUnit.value = saved.unitSystem
                recalculatePattern(notifyUpdate = false)
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _screenStack.value = _screenStack.value + screen
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.value = _screenStack.value.dropLast(1)
            return true
        }
        return false
    }

    fun setUnitSystem(unit: UnitSystem) {
        measurementUnit.value = unit
        val current = currentMeasurements.value
        currentMeasurements.value = if (unit == UnitSystem.INCHES) current.toInches() else current.toCm()
        recalculatePattern(notifyUpdate = true)
    }

    fun updateMeasurement(key: String, value: Float) {
        val cur = currentMeasurements.value
        currentMeasurements.value = when (key) {
            "bust" -> cur.copy(bust = value)
            "waist" -> cur.copy(waist = value)
            "hip" -> cur.copy(hip = value)
            "shoulderWidth" -> cur.copy(shoulderWidth = value)
            "neckCircumference" -> cur.copy(neckCircumference = value)
            "bustPoint" -> cur.copy(bustPoint = value)
            "bustHeight" -> cur.copy(bustHeight = value)
            "shoulderToWaist" -> cur.copy(shoulderToWaist = value)
            "backWaistLength" -> cur.copy(backWaistLength = value)
            "armholeDepth" -> cur.copy(armholeDepth = value)
            "upperArmCircumference" -> cur.copy(upperArmCircumference = value)
            "sleeveLength" -> cur.copy(sleeveLength = value)
            "wristCircumference" -> cur.copy(wristCircumference = value)
            "desiredGarmentLength" -> cur.copy(desiredGarmentLength = value)
            else -> cur
        }
        recalculatePattern(notifyUpdate = true)
    }

    fun saveMeasurementsToProfile() {
        viewModelScope.launch {
            repository.saveMeasurements(currentMeasurements.value)
        }
    }

    fun addUploadedBitmap(bitmap: Bitmap) {
        uploadedBitmaps.value = uploadedBitmaps.value + bitmap
    }

    fun removeUploadedBitmap(index: Int) {
        val list = uploadedBitmaps.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            uploadedBitmaps.value = list
        }
    }

    fun startGarmentAnalysis() {
        if (uploadedBitmaps.value.isEmpty()) {
            analysisError.value = "Please upload at least one garment photo."
            return
        }

        viewModelScope.launch {
            isAnalyzing.value = true
            analysisError.value = null

            try {
                val analysis = GeminiGarmentAnalyzer.analyzeGarment(
                    images = uploadedBitmaps.value,
                    selectedType = selectedGarmentType.value,
                    userNotes = userNotes.value
                )
                currentAnalysis.value = analysis
                activeProjectName.value = analysis.garmentType

                // Recalculate pattern with new garment type & length
                recalculatePattern(notifyUpdate = false)
                navigateTo(AppScreen.ANALYSIS_RESULT)
            } catch (e: Exception) {
                analysisError.value = "Analysis failed: ${e.localizedMessage ?: "Unknown error"}. Try another photo."
            } finally {
                isAnalyzing.value = false
            }
        }
    }

    fun recalculatePattern(notifyUpdate: Boolean = true) {
        val type = if (selectedGarmentType.value != GarmentType.AUTO_IDENTIFY) {
            selectedGarmentType.value
        } else {
            // Infer from analysis
            val text = currentAnalysis.value?.garmentType?.lowercase() ?: ""
            when {
                text.contains("kaba") || text.contains("slit") -> GarmentType.KABA_AND_SLIT
                text.contains("skirt") -> GarmentType.SKIRT
                text.contains("blouse") || text.contains("shirt") -> GarmentType.BLOUSE
                text.contains("kaftan") -> GarmentType.KAFTAN
                else -> GarmentType.DRESS
            }
        }

        val result = PatternEngine.calculatePattern(
            measurements = currentMeasurements.value,
            garmentType = type,
            customEaseInches = customEaseInches.value,
            seamAllowanceInches = customSeamAllowanceInches.value,
            customGarmentLengthInches = customGarmentLengthInches.value
        )

        currentPieces.value = result.first
        calculationBreakdowns.value = result.second

        // Fabric requirements calculation
        val gLength = customGarmentLengthInches.value ?: currentMeasurements.value.toInches().desiredGarmentLength
        fabricEstimation.value = PatternEngine.calculateFabricRequirements(
            measurements = currentMeasurements.value,
            fabricWidthInches = fabricWidthInches.value,
            garmentLengthInches = gLength,
            hasSleeves = type != GarmentType.SKIRT,
            matchPrints = matchPrints.value
        )

        if (notifyUpdate) {
            patternVersion.value += 1
            patternUpdatedNotice.value = true
            viewModelScope.launch {
                delay(3000)
                patternUpdatedNotice.value = false
            }
        }
    }

    fun recordTestGarmentAdjustment(note: String) {
        if (note.isBlank()) return
        testGarmentLog.value = testGarmentLog.value + note
        testGarmentInput.value = ""

        // Parse common adjustment hints (e.g. "reduce waist by 1 in", "add 2 cm to sleeve")
        val lower = note.lowercase()
        val curM = currentMeasurements.value
        when {
            lower.contains("reduce waist") || lower.contains("take in waist") -> {
                val amt = if (lower.contains("cm")) 0.8f else 1.0f
                updateMeasurement("waist", (curM.waist - amt).coerceAtLeast(20f))
            }
            lower.contains("add waist") || lower.contains("increase waist") -> {
                val amt = if (lower.contains("cm")) 0.8f else 1.0f
                updateMeasurement("waist", curM.waist + amt)
            }
            lower.contains("longer sleeve") || lower.contains("add sleeve") -> {
                val amt = if (lower.contains("cm")) 1.0f else 1.5f
                updateMeasurement("sleeveLength", curM.sleeveLength + amt)
            }
            lower.contains("longer length") || lower.contains("add length") -> {
                val amt = if (lower.contains("cm")) 2.0f else 2.0f
                updateMeasurement("desiredGarmentLength", curM.desiredGarmentLength + amt)
            }
        }
    }

    fun askTeacher(question: String) {
        if (question.isBlank()) return
        val currentLog = teacherMessages.value.toMutableList()
        currentLog.add("user" to question)
        teacherMessages.value = currentLog

        viewModelScope.launch {
            isTeacherThinking.value = true
            val m = currentMeasurements.value
            val mSummary = "Bust ${m.bust}\", Waist ${m.waist}\", Hip ${m.hip}\""
            val piecesSummary = currentPieces.value.joinToString(", ") { "${it.name} (${it.cutQuantity})" }

            val reply = GeminiGarmentAnalyzer.askSewingTeacher(
                question = question,
                currentGarmentType = currentAnalysis.value?.garmentType ?: selectedGarmentType.value.displayName,
                measurementsSummary = mSummary,
                currentPatternSummary = piecesSummary
            )

            currentLog.add("teacher" to reply)
            teacherMessages.value = currentLog
            isTeacherThinking.value = false
        }
    }

    fun saveCurrentProject() {
        viewModelScope.launch {
            val analysis = currentAnalysis.value
            val entity = ProjectEntity(
                id = activeProjectId.value ?: 0,
                name = activeProjectName.value.ifBlank { "SewSmart Project" },
                garmentType = selectedGarmentType.value.displayName,
                notes = userNotes.value,
                testGarmentAdjustments = testGarmentLog.value.joinToString("\n"),
                currentVersion = patternVersion.value,
                analysisSummary = analysis?.silhouette ?: "Pattern Draft",
                difficulty = analysis?.difficulty ?: "Intermediate",
                estimatedHours = analysis?.estimatedSewingHours ?: "4 – 6 hours",
                easeInches = customEaseInches.value,
                seamAllowanceInches = customSeamAllowanceInches.value,
                fabricYards = fabricEstimation.value?.estimatedYards ?: 3.0f
            )

            val id = repository.saveProject(entity)
            activeProjectId.value = id
            projectSavedNotice.value = true
            delay(3000)
            projectSavedNotice.value = false
        }
    }

    fun loadProject(project: ProjectEntity) {
        activeProjectId.value = project.id
        activeProjectName.value = project.name
        userNotes.value = project.notes
        testGarmentLog.value = if (project.testGarmentAdjustments.isNotBlank()) project.testGarmentAdjustments.split("\n") else emptyList()
        patternVersion.value = project.currentVersion
        customEaseInches.value = project.easeInches
        customSeamAllowanceInches.value = project.seamAllowanceInches

        // Match garment type enum
        val matchedType = GarmentType.values().firstOrNull { it.displayName == project.garmentType } ?: GarmentType.DRESS
        selectedGarmentType.value = matchedType

        recalculatePattern(notifyUpdate = false)
        navigateTo(AppScreen.PATTERN_STEP_3)
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }
}
