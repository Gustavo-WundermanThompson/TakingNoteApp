package com.gdevs.noteapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdevs.noteapp.core.util.TestTags
import com.gdevs.noteapp.di.AppModule
import com.gdevs.noteapp.presentation.add_edit_note.AddEditNoteScreen
import com.gdevs.noteapp.presentation.notes.NotesScreen
import com.gdevs.noteapp.presentation.util.Screen
import com.gdevs.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule =
        HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            NoteAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.NotesScreen.route
                ) {
                    composable(
                        route = Screen.NotesScreen.route
                    ) {
                        NotesScreen(
                            navController = navController
                        )
                    }
                    composable(
                        route = Screen.AddEditNoteScreen.route + "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        //Click on FAB to get to add note screen
        composeRule.onNodeWithContentDescription("Add note").performClick()
        //next screen
        //Enter text in title and content
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput("test-content")
        composeRule.onNodeWithContentDescription("Save note").performClick() //save note
        //back screen
        //make sure note is in the list
        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        composeRule.onNodeWithText("test-title").performClick() //click on note to edit it
        //note screen
        //make sure note has the same title and content as before
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).assertTextEquals("test-title")
        composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).assertTextEquals("test-content")
        composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput("2")//edit title
        composeRule.onNodeWithContentDescription("Save note").performClick() //save note again
        //back screen
        //make sure note is updated
        composeRule.onNodeWithText("test-title2").assertIsDisplayed()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        for (i in 1..3) {
            composeRule.onNodeWithContentDescription("Add note").performClick()
            //next screen
            composeRule.onNodeWithTag(TestTags.TITLE_TEXT_FIELD).performTextInput(i.toString())
            composeRule.onNodeWithTag(TestTags.CONTENT_TEXT_FIELD).performTextInput(i.toString())
            composeRule.onNodeWithContentDescription("Save note").performClick() //save note
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Sort").performClick()
        composeRule.onNodeWithContentDescription("Title").performClick()
        composeRule.onNodeWithContentDescription("Descending").performClick()

        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[0]
            .assertTextContains("3")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule.onAllNodesWithTag(TestTags.NOTE_ITEM)[2]
            .assertTextContains("1")
    }

}