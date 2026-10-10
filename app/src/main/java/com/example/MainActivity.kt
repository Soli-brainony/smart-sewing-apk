package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AskAiTeacherBottomSheet
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SewSmartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: SewSmartViewModel = viewModel()
                SewSmartApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SewSmartApp(viewModel: SewSmartViewModel) {
    val screenStack by viewModel.screenStack.collectAsState()
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.HOME
    val isTeacherSheetOpen by viewModel.isTeacherSheetOpen.collectAsState()

    // Handle physical / system back button
    BackHandler(enabled = screenStack.size > 1) {
        viewModel.navigateBack()
    }

    val isRootNavScreen = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.PROJECTS_LIST,
        AppScreen.MEASUREMENTS_STEP_2,
        AppScreen.DICTIONARY
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isRootNavScreen) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.UPLOAD_STEP_1,
                        onClick = { viewModel.navigateTo(AppScreen.UPLOAD_STEP_1) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.UPLOAD_STEP_1) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                                contentDescription = "New Garment"
                            )
                        },
                        label = { Text("Analyze") },
                        modifier = Modifier.testTag("nav_analyze")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.MEASUREMENTS_STEP_2,
                        onClick = { viewModel.navigateTo(AppScreen.MEASUREMENTS_STEP_2) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.MEASUREMENTS_STEP_2) Icons.Filled.Straighten else Icons.Outlined.Straighten,
                                contentDescription = "Measurements"
                            )
                        },
                        label = { Text("Measure") },
                        modifier = Modifier.testTag("nav_measure")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROJECTS_LIST,
                        onClick = { viewModel.navigateTo(AppScreen.PROJECTS_LIST) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.PROJECTS_LIST) Icons.Filled.Folder else Icons.Outlined.Folder,
                                contentDescription = "Projects"
                            )
                        },
                        label = { Text("Projects") },
                        modifier = Modifier.testTag("nav_projects")
                    )
                }
            }
        },
        floatingActionButton = {
            // Floating Ask SewSmart AI button - shown only on study & construction screens
            // Excluded on Upload/Analyze screen so it NEVER covers the Analyze button!
            val showFab = currentScreen in listOf(
                AppScreen.PATTERN_STEP_3,
                AppScreen.SEWING_GUIDE_STEP_5,
                AppScreen.DICTIONARY,
                AppScreen.FABRIC_GUIDE
            )
            if (showFab) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.isTeacherSheetOpen.value = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                    text = { Text("Ask SewSmart AI") },
                    modifier = Modifier
                        .padding(bottom = if (currentScreen == AppScreen.DICTIONARY) 0.dp else 64.dp)
                        .testTag("ask_ai_teacher_fab")
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.UPLOAD_STEP_1 -> UploadScreen(viewModel = viewModel)
                AppScreen.ANALYSIS_RESULT -> AnalysisResultScreen(viewModel = viewModel)
                AppScreen.MEASUREMENTS_STEP_2 -> MeasurementsScreen(viewModel = viewModel)
                AppScreen.PATTERN_STEP_3 -> PatternPreviewScreen(viewModel = viewModel)
                AppScreen.CUTTING_PLAN_STEP_4 -> CuttingPlanScreen(viewModel = viewModel)
                AppScreen.SEWING_GUIDE_STEP_5 -> SewingGuideScreen(viewModel = viewModel)
                AppScreen.PRINTABLE_PATTERN -> PrintablePatternScreen(viewModel = viewModel)
                AppScreen.PROJECTS_LIST -> ProjectsListScreen(viewModel = viewModel)
                AppScreen.DICTIONARY -> SewingDictionaryScreen(viewModel = viewModel)
                AppScreen.FABRIC_GUIDE -> FabricGuideScreen(viewModel = viewModel)
            }
        }

        // Ask AI Teacher Bottom Sheet
        if (isTeacherSheetOpen) {
            AskAiTeacherBottomSheet(
                viewModel = viewModel,
                onDismiss = { viewModel.isTeacherSheetOpen.value = false }
            )
        }
    }
}
