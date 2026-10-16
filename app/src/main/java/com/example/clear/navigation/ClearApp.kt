package com.example.clear.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clear.model.CheckInCategory
import com.example.clear.model.Meditations
import com.example.clear.model.Situations
import com.example.clear.ui.akut.AkutScreen
import com.example.clear.ui.akut.EmergencyCardScreen
import com.example.clear.ui.akut.ExerciseScreen
import com.example.clear.ui.akut.MusicListenScreen
import com.example.clear.ui.akut.SituationScreen
import com.example.clear.ui.anker.AnkerScreen
import com.example.clear.ui.anker.EmergencyEditScreen
import com.example.clear.ui.anker.ImageViewerScreen
import com.example.clear.ui.anker.ImagesScreen
import com.example.clear.ui.anker.MusicScreen
import com.example.clear.ui.anker.RemindersScreen
import com.example.clear.ui.anker.LetterEditorScreen
import com.example.clear.ui.anker.LetterReaderScreen
import com.example.clear.ui.anker.LettersScreen
import com.example.clear.ui.anker.QuoteEditorScreen
import com.example.clear.ui.anker.QuotesScreen
import com.example.clear.ui.checkin.CategoryDetailScreen
import com.example.clear.ui.checkin.CheckInFlowScreen
import com.example.clear.ui.checkin.CheckInStartScreen
import com.example.clear.ui.checkin.IdeaEditorScreen
import com.example.clear.ui.meditation.MeditationPlayerScreen
import com.example.clear.ui.meditation.MeditationScreen
import com.example.clear.ui.settings.AboutScreen
import com.example.clear.ui.settings.AppearanceScreen
import com.example.clear.ui.settings.CheckInReminderScreen
import com.example.clear.ui.settings.MeditationReminderScreen
import com.example.clear.ui.settings.MyDataScreen
import com.example.clear.ui.settings.ReminderSettingsScreen
import com.example.clear.ui.settings.SettingsScreen
import com.example.clear.ui.theme.ClearPrimary
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark
import com.example.clear.ui.theme.ClearTextSecondary

// Eigene Route fuer die Notfallkarte-Bearbeitung, wenn sie aus dem Akut-Tab geoeffnet wird.
// So bleibt der Akut-Tab in der Leiste markiert.
private const val AKUT_EMERGENCY_EDIT = "akut_notfallkarte_bearbeiten"
private const val AKUT_MUSIC = "akut_musik/{id}"

// Auf diesen Seiten ist die untere Leiste ausgeblendet (ruhiger, Platz fuer Tastatur).
private val routesWithoutBottomBar = setOf(
    CheckInRoutes.FLOW, CheckInRoutes.EDITOR,
    AnkerRoutes.QUOTE_EDITOR, AnkerRoutes.LETTER_EDITOR,
    SettingsRoutes.EMERGENCY_EDIT, AKUT_EMERGENCY_EDIT
)

// Startseite jedes Tabs. Wird fuer "erneut auf den Tab tippen" gebraucht.
private fun MainTab.startRoute(): String = when (this) {
    MainTab.CheckIn -> CheckInRoutes.START
    MainTab.Meditieren -> MeditationRoutes.START
    MainTab.Akut -> AkutRoutes.START
    MainTab.Anker -> AnkerRoutes.START
    MainTab.Einstellungen -> SettingsRoutes.START
}

@Composable
fun ClearApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentRoute = destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute !in routesWithoutBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    MainTab.entries.forEach { tab ->
                        val isSelected =
                            destination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    // Erneuter Tipp: zurueck zur Hauptansicht dieses Bereichs
                                    navController.popBackStack(tab.startRoute(), inclusive = false)
                                } else {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ClearPrimary,
                                selectedTextColor = ClearTextDark,
                                unselectedIconColor = ClearTextSecondary,
                                unselectedTextColor = ClearTextSecondary,
                                indicatorColor = ClearSurfaceSoft
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.CheckIn.route,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {
            // --- Check In ---
            navigation(startDestination = CheckInRoutes.START, route = MainTab.CheckIn.route) {
                composable(CheckInRoutes.START) {
                    CheckInStartScreen(
                        onStartCheckIn = { navController.navigate(CheckInRoutes.FLOW) },
                        onOpenCategory = { navController.navigate(CheckInRoutes.category(it.id)) }
                    )
                }
                composable(CheckInRoutes.FLOW) {
                    CheckInFlowScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
                composable(
                    route = CheckInRoutes.CATEGORY,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val category = CheckInCategory.fromId(entry.arguments?.getString("id"))
                    if (category != null) {
                        CategoryDetailScreen(
                            category = category,
                            onBack = { navController.popBackStack() },
                            onAddIdea = {
                                navController.navigate(CheckInRoutes.editor(category.id))
                            },
                            onEditIdea = { ideaId ->
                                navController.navigate(CheckInRoutes.editor(category.id, ideaId))
                            }
                        )
                    }
                }
                composable(
                    route = CheckInRoutes.EDITOR,
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.StringType },
                        navArgument("ideaId") { type = NavType.LongType }
                    )
                ) {
                    IdeaEditorScreen(onClose = { navController.popBackStack() })
                }
            }

            // --- Meditieren ---
            navigation(startDestination = MeditationRoutes.START, route = MainTab.Meditieren.route) {
                composable(MeditationRoutes.START) {
                    MeditationScreen(
                        onOpenMeditation = { navController.navigate(MeditationRoutes.player(it.id)) }
                    )
                }
                composable(
                    route = MeditationRoutes.PLAYER,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val meditation = Meditations.fromId(entry.arguments?.getString("id"))
                    if (meditation != null) {
                        MeditationPlayerScreen(
                            meditation = meditation,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            // --- Akut ---
            navigation(startDestination = AkutRoutes.START, route = MainTab.Akut.route) {
                composable(AkutRoutes.START) {
                    AkutScreen(
                        onOpenSituation = { navController.navigate(AkutRoutes.situation(it.id)) },
                        onOpenEmergency = { navController.navigate(AkutRoutes.EMERGENCY) }
                    )
                }
                composable(
                    route = AkutRoutes.SITUATION,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val situation = Situations.fromId(entry.arguments?.getString("id"))
                    if (situation != null) {
                        SituationScreen(
                            situation = situation,
                            onBack = { navController.popBackStack() },
                            onOpenExercise = { navController.navigate(AkutRoutes.exercise(it.id)) },
                            onOpenMusic = { navController.navigate("akut_musik/${situation.id}") }
                        )
                    }
                }
                composable(
                    route = AkutRoutes.EXERCISE,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) { entry ->
                    val exercise = Situations.findExercise(entry.arguments?.getString("id"))
                    if (exercise != null) {
                        ExerciseScreen(
                            exercise = exercise,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
                composable(AkutRoutes.EMERGENCY) {
                    EmergencyCardScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(AKUT_EMERGENCY_EDIT) }
                    )
                }
                composable(AKUT_EMERGENCY_EDIT) {
                    EmergencyEditScreen(onClose = { navController.popBackStack() })
                }
                composable(
                    route = AKUT_MUSIC,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    MusicListenScreen(onBack = { navController.popBackStack() })
                }
            }

            // --- Mein Anker ---
            navigation(startDestination = AnkerRoutes.START, route = MainTab.Anker.route) {
                composable(AnkerRoutes.START) {
                    AnkerScreen(
                        onOpenQuotes = { navController.navigate(AnkerRoutes.QUOTES) },
                        onOpenLetters = { navController.navigate(AnkerRoutes.LETTERS) },
                        onOpenImages = { navController.navigate(AnkerRoutes.IMAGES) },
                        onOpenMusic = { navController.navigate(AnkerRoutes.MUSIC) }
                    )
                }
                composable(AnkerRoutes.QUOTES) {
                    QuotesScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate(AnkerRoutes.quoteEditor()) },
                        onEdit = { navController.navigate(AnkerRoutes.quoteEditor(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.QUOTE_EDITOR,
                    arguments = listOf(navArgument("quoteId") { type = NavType.LongType })
                ) {
                    QuoteEditorScreen(onClose = { navController.popBackStack() })
                }
                composable(AnkerRoutes.LETTERS) {
                    LettersScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate(AnkerRoutes.letterEditor()) },
                        onOpen = { navController.navigate(AnkerRoutes.letterReader(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.LETTER_READER,
                    arguments = listOf(navArgument("letterId") { type = NavType.LongType })
                ) {
                    LetterReaderScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(AnkerRoutes.letterEditor(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.LETTER_EDITOR,
                    arguments = listOf(navArgument("letterId") { type = NavType.LongType })
                ) {
                    LetterEditorScreen(onClose = { navController.popBackStack() })
                }
                composable(AnkerRoutes.IMAGES) {
                    ImagesScreen(
                        onBack = { navController.popBackStack() },
                        onOpenImage = { navController.navigate(AnkerRoutes.imageViewer(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.IMAGE_VIEWER,
                    arguments = listOf(navArgument("imageId") { type = NavType.LongType })
                ) {
                    ImageViewerScreen(onBack = { navController.popBackStack() })
                }
                composable(AnkerRoutes.MUSIC) {
                    MusicScreen(onBack = { navController.popBackStack() })
                }
            }

            // --- Einstellungen ---
            navigation(startDestination = SettingsRoutes.START, route = MainTab.Einstellungen.route) {
                composable(SettingsRoutes.START) {
                    SettingsScreen(
                        onOpenAppearance = { navController.navigate(SettingsRoutes.APPEARANCE) },
                        onOpenReminders = { navController.navigate(SettingsRoutes.REMINDERS) },
                        onOpenEmergency = { navController.navigate(SettingsRoutes.EMERGENCY_EDIT) },
                        onOpenMyData = { navController.navigate(SettingsRoutes.MY_DATA) },
                        onOpenAbout = { navController.navigate(SettingsRoutes.ABOUT) }
                    )
                }
                composable(SettingsRoutes.APPEARANCE) {
                    AppearanceScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.REMINDERS) {
                    ReminderSettingsScreen(
                        onBack = { navController.popBackStack() },
                        onOpenQuoteReminders = { navController.navigate(SettingsRoutes.QUOTE_REMINDERS) },
                        onOpenMeditationReminder = { navController.navigate(SettingsRoutes.MEDITATION_REMINDER) },
                        onOpenCheckInReminder = { navController.navigate(SettingsRoutes.CHECKIN_REMINDER) }
                    )
                }
                composable(SettingsRoutes.MEDITATION_REMINDER) {
                    MeditationReminderScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.CHECKIN_REMINDER) {
                    CheckInReminderScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.QUOTE_REMINDERS) {
                    RemindersScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.EMERGENCY_EDIT) {
                    EmergencyEditScreen(onClose = { navController.popBackStack() })
                }
                composable(SettingsRoutes.MY_DATA) {
                    MyDataScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.ABOUT) {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
