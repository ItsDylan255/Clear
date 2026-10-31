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
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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
import com.example.clear.reminder.ExerciseNotifier
import com.example.clear.tr
import com.example.clear.ui.akut.EmergencyCardScreen
import com.example.clear.ui.akut.ExerciseScreen
import com.example.clear.ui.akut.ThoughtDetailScreen
import com.example.clear.ui.akut.ThoughtFlowScreen
import com.example.clear.ui.akut.ThoughtListScreen
import com.example.clear.ui.akut.SituationScreen
import com.example.clear.ui.anker.AnkerEmotionPickScreen
import com.example.clear.ui.anker.EmergencyManageScreen
import com.example.clear.ui.anker.IdeasManageScreen
import com.example.clear.ui.anker.WarningSignsScreen
import com.example.clear.ui.anker.AnkerType
import com.example.clear.ui.anker.AnkerScreen
import com.example.clear.ui.anker.EveningArchiveScreen
import com.example.clear.ui.anker.EveningEditScreen
import com.example.clear.ui.anker.EmergencyPersonEditScreen
import com.example.clear.ui.anker.EmergencyPlanEditScreen
import com.example.clear.ui.anker.ImageViewerScreen
import com.example.clear.ui.anker.ImagesScreen
import com.example.clear.ui.anker.MusicScreen
import com.example.clear.ui.anker.RemindersScreen
import com.example.clear.ui.anker.LetterEditorScreen
import com.example.clear.ui.anker.LetterReaderScreen
import com.example.clear.ui.anker.LettersScreen
import com.example.clear.ui.anker.QuoteEditorScreen
import com.example.clear.ui.anker.QuotesScreen
import com.example.clear.ui.checkin.ActiveIdeas
import com.example.clear.ui.checkin.CategoryDetailScreen
import com.example.clear.ui.checkin.IdeaDetailScreen
import com.example.clear.ui.home.HomeScreen
import com.example.clear.ui.checkin.IdeaRateScreen
import com.example.clear.ui.checkin.CheckInFlowScreen
import com.example.clear.ui.checkin.HistoryChartScreen
import com.example.clear.ui.checkin.HistoryOverviewScreen
import com.example.clear.ui.checkin.Phq9HubScreen
import com.example.clear.ui.checkin.Phq9TestScreen
import com.example.clear.ui.checkin.QuestionnairesScreen
import com.example.clear.ui.checkin.Who5HubScreen
import com.example.clear.ui.checkin.Who5TestScreen
import com.example.clear.ui.checkin.WsasHubScreen
import com.example.clear.ui.checkin.WsasTestScreen
import com.example.clear.ui.checkin.CheckInStartScreen
import com.example.clear.ui.checkin.IdeaEditorScreen
import com.example.clear.ui.meditation.MeditationPlayerScreen
import com.example.clear.ui.meditation.MeditationScreen
import com.example.clear.ui.settings.AppearanceScreen
import com.example.clear.ui.settings.LanguageScreen
import com.example.clear.ui.settings.CheckInReminderScreen
import com.example.clear.ui.settings.EveningReminderScreen
import com.example.clear.ui.settings.Phq9ReminderScreen
import com.example.clear.ui.settings.Who5ReminderScreen
import com.example.clear.ui.settings.WsasReminderScreen
import com.example.clear.ui.settings.MeditationReminderScreen
import com.example.clear.ui.settings.MyDataScreen
import com.example.clear.ui.settings.FocusSettingsScreen
import com.example.clear.ui.settings.ReminderSettingsScreen
import com.example.clear.ui.settings.SettingsScreen
import com.example.clear.ui.theme.ClearPrimary
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark
import com.example.clear.ui.theme.ClearTextSecondary
import com.example.clear.watch.WatchSync

// Eigene Route fuer die Notfallkarte-Bearbeitung, wenn sie aus dem Akut-Tab geoeffnet wird.
// So bleibt der Akut-Tab in der Leiste markiert.
private const val ANKER_EMERGENCY_PERSON = "anker_notfallperson_bearbeiten"
private const val ANKER_EMERGENCY_PLAN = "anker_notfallplan_bearbeiten"
private const val ANKER_WARNING_SIGNS = "anker_warnzeichen"
// Mein-Anker-Ansicht einer Emotion (gleiche Seiten, eigene Routen je Bereich)
private val akutView = AnkerViewRoutes("akut_ansicht")
private val ankerView = AnkerViewRoutes("anker_ansicht")
// Verlaufsseite im Check-in-Bereich
private const val CHECKIN_HISTORY = "checkin_verlauf"
private const val CHECKIN_HISTORY_CHART = "checkin_verlauf_diagramm/{kind}"
private fun historyChartRoute(kind: String) = "checkin_verlauf_diagramm/$kind"
// Monatlicher Fragebogen (PHQ-9) im Check-in-Bereich
private const val CHECKIN_QUESTIONNAIRES = "checkin_frageboegen"
private const val CHECKIN_PHQ = "checkin_phq"
private const val CHECKIN_PHQ_TEST = "checkin_phq_test"
private const val CHECKIN_WHO5 = "checkin_who5"
private const val CHECKIN_WHO5_TEST = "checkin_who5_test"
private const val CHECKIN_WSAS = "checkin_wsas"
private const val CHECKIN_WSAS_TEST = "checkin_wsas_test"
// Idee im Check-in: Beschreibung und Bewertung (mode = "before" oder "after")
private const val CHECKIN_IDEA = "checkin_idee/{ideaId}"
private const val CHECKIN_IDEA_RATE = "checkin_idee_bewertung/{ideaId}/{mode}"
private fun ideaRoute(id: Long) = "checkin_idee/$id"
private fun ideaRateRoute(id: Long, mode: String) = "checkin_idee_bewertung/$id/$mode"

fun Context.findActivity(): Activity? {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

// Auf diesen Seiten ist die untere Leiste ausgeblendet (ruhiger, Platz fuer Tastatur).
private val routesWithoutBottomBar = setOf(
    CheckInRoutes.FLOW, CheckInRoutes.EDITOR, CHECKIN_PHQ_TEST, CHECKIN_WHO5_TEST, CHECKIN_WSAS_TEST, AkutRoutes.THOUGHT_NEW,
    AnkerRoutes.QUOTE_EDITOR, AnkerRoutes.LETTER_EDITOR,
    AnkerRoutes.EVENING_EDIT,
    ANKER_EMERGENCY_PERSON, ANKER_EMERGENCY_PLAN, CHECKIN_IDEA_RATE
) + akutView.singleViewPatterns + ankerView.singleViewPatterns

private const val HOME_START = "home_start"

/** Die sechs Reiter. Home ist neu, der frühere Check-in-Reiter heisst jetzt "Bereiche". */
private enum class AppTab(val route: String, val icon: ImageVector) {
    Home("tab_home", Icons.Outlined.Home),
    CheckIn(MainTab.CheckIn.route, Icons.Outlined.GridView),
    Meditieren(MainTab.Meditieren.route, MainTab.Meditieren.icon),
    Akut(MainTab.Akut.route, MainTab.Akut.icon),
    Anker(MainTab.Anker.route, MainTab.Anker.icon),
    Einstellungen(MainTab.Einstellungen.route, MainTab.Einstellungen.icon);

    /** Beschriftung in der eingestellten Sprache. */
    val label: String
        get() = when (this) {
            Home -> "Home"
            CheckIn -> tr("Bereiche", "Areas")
            Meditieren -> MainTab.Meditieren.label
            Akut -> MainTab.Akut.label
            Anker -> MainTab.Anker.label
            Einstellungen -> MainTab.Einstellungen.label
        }
}

// Startseite jedes Tabs. Wird fuer "erneut auf den Tab tippen" gebraucht.
private fun AppTab.startRoute(): String = when (this) {
    AppTab.Home -> HOME_START
    AppTab.CheckIn -> CheckInRoutes.START
    AppTab.Meditieren -> MeditationRoutes.START
    AppTab.Akut -> AkutRoutes.START
    AppTab.Anker -> AnkerRoutes.START
    AppTab.Einstellungen -> SettingsRoutes.START
}

@Composable
fun ClearApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val currentRoute = destination?.route

    // Laufende Ideen: Benachrichtigungen wiederherstellen. Kommt man aus einer Benachrichtigung,
    // geht es direkt zur Beschreibung oder zur Bewertung "Wie fuehlst du dich jetzt?".
    val appContext = LocalContext.current
    LaunchedEffect(Unit) {
        androidx.core.app.NotificationManagerCompat.from(appContext)
            .deleteNotificationChannel("experiments_running")
        ExerciseNotifier.cancelLegacy(appContext)
        ExerciseNotifier.sync(appContext)
        WatchSync.update(appContext)

        val launchIntent = appContext.findActivity()?.intent
        val ideaId = launchIntent?.getLongExtra(ExerciseNotifier.EXTRA_IDEA_ID, -1L) ?: -1L
        if (launchIntent != null && ideaId > 0L) {
            val action = launchIntent.getStringExtra(ExerciseNotifier.EXTRA_ACTION)
            launchIntent.removeExtra(ExerciseNotifier.EXTRA_IDEA_ID)
            launchIntent.removeExtra(ExerciseNotifier.EXTRA_ACTION)
            navController.navigate(CheckInRoutes.START) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
            }
            navController.navigate(ideaRoute(ideaId))
            if (action == ExerciseNotifier.ACTION_END) {
                navController.navigate(ideaRateRoute(ideaId, "after"))
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (currentRoute !in routesWithoutBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    AppTab.entries.forEach { tab ->
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
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ClearPrimary,
                                selectedTextColor = ClearTextDark,
                                unselectedIconColor = ClearTextSecondary,
                                unselectedTextColor = ClearTextSecondary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppTab.Home.route,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding),
            // Keine Animationen beim Wechsel der Seiten
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            // --- Home: Check-in, Verlauf und Frageboegen ---
            navigation(startDestination = HOME_START, route = AppTab.Home.route) {
                composable(HOME_START) {
                    HomeScreen(
                        onStartCheckIn = { navController.navigate(CheckInRoutes.FLOW) },
                        onOpenHistory = { navController.navigate(CHECKIN_HISTORY) },
                        onOpenQuestionnaires = { navController.navigate(CHECKIN_QUESTIONNAIRES) },
                        onOpenIdea = { id -> navController.navigate(ideaRoute(id)) }
                    )
                }
                composable(CHECKIN_QUESTIONNAIRES) {
                    QuestionnairesScreen(
                        onBack = { navController.popBackStack() },
                        onOpenPhq = { navController.navigate(CHECKIN_PHQ) },
                        onOpenWho5 = { navController.navigate(CHECKIN_WHO5) },
                        onOpenWsas = { navController.navigate(CHECKIN_WSAS) }
                    )
                }
                composable(CHECKIN_WSAS) {
                    WsasHubScreen(
                        onBack = { navController.popBackStack() },
                        onStart = { navController.navigate(CHECKIN_WSAS_TEST) },
                        onOpenHistory = { navController.navigate(historyChartRoute("wsas")) }
                    )
                }
                composable(CHECKIN_WSAS_TEST) {
                    WsasTestScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
                composable(CHECKIN_WHO5) {
                    Who5HubScreen(
                        onBack = { navController.popBackStack() },
                        onStart = { navController.navigate(CHECKIN_WHO5_TEST) },
                        onOpenHistory = { navController.navigate(historyChartRoute("who5")) }
                    )
                }
                composable(CHECKIN_WHO5_TEST) {
                    Who5TestScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
                composable(CHECKIN_PHQ) {
                    Phq9HubScreen(
                        onBack = { navController.popBackStack() },
                        onStart = { navController.navigate(CHECKIN_PHQ_TEST) },
                        onOpenHistory = { navController.navigate(historyChartRoute("phq9")) }
                    )
                }
                composable(CHECKIN_PHQ_TEST) {
                    Phq9TestScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
                composable(CHECKIN_HISTORY) {
                    HistoryOverviewScreen(
                        onBack = { navController.popBackStack() },
                        onOpen = { kind -> navController.navigate(historyChartRoute(kind)) }
                    )
                }
                composable(
                    route = CHECKIN_HISTORY_CHART,
                    arguments = listOf(navArgument("kind") { type = NavType.StringType })
                ) {
                    HistoryChartScreen(onBack = { navController.popBackStack() })
                }
                composable(CheckInRoutes.FLOW) {
                    CheckInFlowScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
            }

            // --- Bereiche (frueher Check In) ---
            navigation(startDestination = CheckInRoutes.START, route = AppTab.CheckIn.route) {
                composable(CheckInRoutes.START) {
                    CheckInStartScreen(
                        onOpenCategory = { navController.navigate(CheckInRoutes.category(it.id)) }
                    )
                }
                composable(
                    route = CHECKIN_IDEA,
                    arguments = listOf(navArgument("ideaId") { type = NavType.LongType })
                ) { entry ->
                    IdeaDetailScreen(
                        onBack = { navController.popBackStack() },
                        onStart = { id -> navController.navigate(ideaRateRoute(id, "before")) },
                        onEnd = { id -> navController.navigate(ideaRateRoute(id, "after")) }
                    )
                }
                composable(
                    route = CHECKIN_IDEA_RATE,
                    arguments = listOf(
                        navArgument("ideaId") { type = NavType.LongType },
                        navArgument("mode") { type = NavType.StringType }
                    )
                ) {
                    IdeaRateScreen(
                        onBack = { navController.popBackStack() },
                        onDone = { navController.popBackStack() }
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
                            onOpenIdea = { ideaId -> navController.navigate(ideaRoute(ideaId)) },
                            onOpenHistory = { navController.navigate(historyChartRoute(category.id)) }
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
            navigation(startDestination = MeditationRoutes.START, route = AppTab.Meditieren.route) {
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
            navigation(startDestination = AkutRoutes.START, route = AppTab.Akut.route) {
                composable(AkutRoutes.START) {
                    AkutScreen(
                        onOpenSituation = { navController.navigate(AkutRoutes.situation(it.id)) },
                        onOpenThoughts = { navController.navigate(AkutRoutes.THOUGHTS) },
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
                            onOpenAnker = { navController.navigate(akutView.view(situation.id)) }
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
                    EmergencyCardScreen(onBack = { navController.popBackStack() })
                }
                composable(AkutRoutes.THOUGHTS) {
                    ThoughtListScreen(
                        onBack = { navController.popBackStack() },
                        onNew = { navController.navigate(AkutRoutes.THOUGHT_NEW) },
                        onOpen = { navController.navigate(AkutRoutes.thoughtDetail(it)) }
                    )
                }
                composable(AkutRoutes.THOUGHT_NEW) {
                    ThoughtFlowScreen(
                        onClose = { navController.popBackStack() },
                        onFinished = { navController.popBackStack() }
                    )
                }
                composable(
                    route = AkutRoutes.THOUGHT_DETAIL,
                    arguments = listOf(navArgument("recordId") { type = NavType.LongType })
                ) {
                    ThoughtDetailScreen(onBack = { navController.popBackStack() })
                }
                ankerViewRoutes(navController, akutView)
            }

            // --- Mein Anker ---
            navigation(startDestination = AnkerRoutes.START, route = AppTab.Anker.route) {
                composable(AnkerRoutes.START) {
                    AnkerScreen(
                        onOpenEvening = { navController.navigate(AnkerRoutes.EVENING) },
                        onOpenIdeas = { navController.navigate(AnkerRoutes.IDEAS) },
                        onOpenEmergency = { navController.navigate(AnkerRoutes.EMERGENCY) },
                        onOpenType = { navController.navigate(AnkerRoutes.pick(it.id)) }
                    )
                }
                composable(AnkerRoutes.IDEAS) {
                    IdeasManageScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { categoryId -> navController.navigate(CheckInRoutes.editor(categoryId)) },
                        onEdit = { categoryId, ideaId ->
                            navController.navigate(CheckInRoutes.editor(categoryId, ideaId))
                        }
                    )
                }
                composable(AnkerRoutes.EVENING) {
                    EveningArchiveScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(AnkerRoutes.eveningEdit(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.EVENING_EDIT,
                    arguments = listOf(navArgument("entryId") { type = NavType.LongType })
                ) {
                    EveningEditScreen(onClose = { navController.popBackStack() })
                }
                composable(AnkerRoutes.EMERGENCY) {
                    EmergencyManageScreen(
                        onBack = { navController.popBackStack() },
                        onEditPerson = { navController.navigate(ANKER_EMERGENCY_PERSON) },
                        onEditPlan = { navController.navigate(ANKER_EMERGENCY_PLAN) },
                        onEditWarningSigns = { navController.navigate(ANKER_WARNING_SIGNS) }
                    )
                }
                composable(ANKER_EMERGENCY_PERSON) {
                    EmergencyPersonEditScreen(onClose = { navController.popBackStack() })
                }
                composable(ANKER_EMERGENCY_PLAN) {
                    EmergencyPlanEditScreen(onClose = { navController.popBackStack() })
                }
                composable(ANKER_WARNING_SIGNS) {
                    WarningSignsScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = AnkerRoutes.PICK,
                    arguments = listOf(navArgument("type") { type = NavType.StringType })
                ) { entry ->
                    val ankerType = AnkerType.fromId(entry.arguments?.getString("type"))
                    if (ankerType != null) {
                        AnkerEmotionPickScreen(
                            type = ankerType,
                            onBack = { navController.popBackStack() },
                            onPick = { emotion ->
                                navController.navigate(
                                    when (ankerType) {
                                        AnkerType.Quotes -> AnkerRoutes.quotes(emotion)
                                        AnkerType.Images -> AnkerRoutes.images(emotion)
                                        AnkerType.Music -> AnkerRoutes.music(emotion)
                                        AnkerType.Letters -> AnkerRoutes.letters(emotion)
                                    }
                                )
                            }
                        )
                    }
                }
                ankerViewRoutes(navController, ankerView)
                composable(
                    route = AnkerRoutes.QUOTES,
                    arguments = listOf(navArgument("emotion") { type = NavType.StringType })
                ) { entry ->
                    val emotion = entry.arguments?.getString("emotion") ?: "unknown"
                    QuotesScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate(AnkerRoutes.quoteEditor(-1L, emotion)) },
                        onEdit = { navController.navigate(AnkerRoutes.quoteEditor(it, emotion)) }
                    )
                }
                composable(
                    route = AnkerRoutes.QUOTE_EDITOR,
                    arguments = listOf(
                        navArgument("quoteId") { type = NavType.LongType },
                        navArgument("emotion") { type = NavType.StringType }
                    )
                ) {
                    QuoteEditorScreen(onClose = { navController.popBackStack() })
                }
                composable(
                    route = AnkerRoutes.LETTERS,
                    arguments = listOf(navArgument("emotion") { type = NavType.StringType })
                ) { entry ->
                    val emotion = entry.arguments?.getString("emotion") ?: "unknown"
                    LettersScreen(
                        onBack = { navController.popBackStack() },
                        onAdd = { navController.navigate(AnkerRoutes.letterEditor(-1L, emotion)) },
                        onOpen = { navController.navigate(AnkerRoutes.letterReader(it)) }
                    )
                }
                composable(
                    route = AnkerRoutes.LETTER_READER,
                    arguments = listOf(navArgument("letterId") { type = NavType.LongType })
                ) {
                    LetterReaderScreen(
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(AnkerRoutes.letterEditor(it, "unknown")) }
                    )
                }
                composable(
                    route = AnkerRoutes.LETTER_EDITOR,
                    arguments = listOf(
                        navArgument("letterId") { type = NavType.LongType },
                        navArgument("emotion") { type = NavType.StringType }
                    )
                ) {
                    LetterEditorScreen(onClose = { navController.popBackStack() })
                }
                composable(
                    route = AnkerRoutes.IMAGES,
                    arguments = listOf(navArgument("emotion") { type = NavType.StringType })
                ) {
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
                composable(
                    route = AnkerRoutes.MUSIC,
                    arguments = listOf(navArgument("emotion") { type = NavType.StringType })
                ) {
                    MusicScreen(onBack = { navController.popBackStack() })
                }
            }

            // --- Einstellungen ---
            navigation(startDestination = SettingsRoutes.START, route = AppTab.Einstellungen.route) {
                composable(SettingsRoutes.START) {
                    SettingsScreen(
                        onOpenAppearance = { navController.navigate(SettingsRoutes.APPEARANCE) },
                        onOpenLanguage = { navController.navigate(SettingsRoutes.LANGUAGE) },
                        onOpenReminders = { navController.navigate(SettingsRoutes.REMINDERS) },
                        onOpenFocus = { navController.navigate(SettingsRoutes.FOCUS) },
                        onOpenMyData = { navController.navigate(SettingsRoutes.MY_DATA) }
                    )
                }
                composable(SettingsRoutes.FOCUS) {
                    FocusSettingsScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.APPEARANCE) {
                    AppearanceScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.LANGUAGE) {
                    LanguageScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.REMINDERS) {
                    ReminderSettingsScreen(
                        onBack = { navController.popBackStack() },
                        onOpenQuoteReminders = { navController.navigate(SettingsRoutes.QUOTE_REMINDERS) },
                        onOpenMeditationReminder = { navController.navigate(SettingsRoutes.MEDITATION_REMINDER) },
                        onOpenCheckInReminder = { navController.navigate(SettingsRoutes.CHECKIN_REMINDER) },
                        onOpenPhqReminder = { navController.navigate(SettingsRoutes.PHQ_REMINDER) },
                        onOpenWho5Reminder = { navController.navigate(SettingsRoutes.WHO5_REMINDER) },
                        onOpenWsasReminder = { navController.navigate(SettingsRoutes.WSAS_REMINDER) },
                        onOpenEveningReminder = { navController.navigate(SettingsRoutes.EVENING_REMINDER) }
                    )
                }
                composable(SettingsRoutes.WSAS_REMINDER) {
                    WsasReminderScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.WHO5_REMINDER) {
                    Who5ReminderScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.PHQ_REMINDER) {
                    Phq9ReminderScreen(onBack = { navController.popBackStack() })
                }
                composable(SettingsRoutes.EVENING_REMINDER) {
                    EveningReminderScreen(onBack = { navController.popBackStack() })
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
                composable(SettingsRoutes.MY_DATA) {
                    MyDataScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
