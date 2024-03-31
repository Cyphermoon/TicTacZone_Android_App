package com.cyphermoon.tictaczone.presentation.game_flow.utils

import com.cyphermoon.tictaczone.redux.GamePlayerProps
import android.app.AlertDialog
import android.content.Context
import com.cyphermoon.tictaczone.redux.LocalPlayActions
import com.cyphermoon.tictaczone.redux.store
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

fun checkWinningMove(board: Map<String, String>, currentPlayerMarker: String): Boolean {
    // Define the winning combinations
    val winningCombinations = listOf(
        listOf("1", "2", "3"), // Top row
        listOf("4", "5", "6"), // Middle row
        listOf("7", "8", "9"), // Bottom row
        listOf("1", "4", "7"), // Left column
        listOf("2", "5", "8"), // Middle column
        listOf("3", "6", "9"), // Right column
        listOf("1", "5", "9"), // Diagonal from top-left to bottom-right
        listOf("3", "5", "7")  // Diagonal from top-right to bottom-left
    )

    // Check each winning combination
    for (combination in winningCombinations) {
        if (combination.all { board[it] == currentPlayerMarker }) {
            // If all cells in a combination contain the current player's marker, return true
            return true
        }
    }

    // If no winning combination is found, return false
    return false
}

fun checkIfBoardIsFull(board: Map<String, String>): Boolean {
    // Iterate over all cells in the board
    for (cell in board.values) {
        // If a cell is not marked by a player (i.e., it is empty), return false
        if (cell.isEmpty()) {
            return false
        }
    }

    // If no empty cell is found, return true
    return true
}

/**
 * Resets the game board to its initial state.
 * @return A map representing the initial state of the game board.
 */
fun resetBoard(): Map<String, String> {
    return mapOf(
        "1" to "",
        "2" to "",
        "3" to "",
        "4" to "",
        "5" to "",
        "6" to "",
        "7" to "",
        "8" to "",
        "9" to ""
    )
}

/**
 * Returns a list of all the empty cells in the game board.
 * @param board The current state of the game board.
 * @return A list of keys representing the empty cells in the game board.
 */
fun getEmptyCells(board: Map<String, String>): List<String> {
    return board.filter { it.value.isEmpty() }.keys.toList()
}

/**
 * Checks if the game is over.
 * @param board The current state of the game board.
 * @return True if the game is over, false otherwise.
 */
fun isGameOver(board: Map<String, String>): Boolean {
    return checkWinningMove(board, "X") || checkWinningMove(board, "O") || checkIfBoardIsFull(board)
}

/**
 * Returns the winner of the game if there is one.
 * @param board The current state of the game board.
 * @return The marker of the winning player if there is one, null otherwise.
 */
fun getWinner(board: Map<String, String>): String? {
    return when {
        checkWinningMove(board, "X") -> "X"
        checkWinningMove(board, "O") -> "O"
        else -> null
    }
}

/**
 * Checks if a move is valid.
 * @param board The current state of the game board.
 * @param position The position of the move.
 * @return True if the move is valid, false otherwise.
 */
fun isValidMove(board: Map<String, String>, position: String): Boolean {
    return board[position].isNullOrEmpty()
}



/**
 * This function shows a dialog with a title and a message.
 * @param context The context in which the dialog should be shown.
 * @param title The title of the dialog.
 * @param message The message of the dialog.
 */
fun showGameStateDialog(context: Context, title: String, message: String, resetBoard: (() -> Unit)? = null) {
    // Create an AlertDialog builder with the given context
    AlertDialog.Builder(context)
        .setTitle(title) // Set the title of the dialog
        .setMessage(message) // Set the message of the dialog
        .setPositiveButton("OK") { dialog, _ ->
            // Set a positive button with the text "OK"
            // When this button is clicked, dismiss the dialog
            if(resetBoard !== null) resetBoard()
            dialog.dismiss()
        }
        .setCancelable(false)
        .create() // Create the AlertDialog
        .show() // Show the AlertDialog
}

// TimerUtils is a utility object that provides functions to start and stop a timer.
object TimerUtils {
    // A nullable Timer variable that will hold the instance of the timer when it's started.
    var timer: Timer? = null

    // Function to start the timer. It takes a lambda function as a parameter which will be called every tick.
    fun startTimer(onTick: (TimerTask) -> Unit) {
        // Create a new Timer instance and assign it to the timer variable.
        timer = Timer()
        // Schedule a new TimerTask to run after a delay and at fixed intervals.
        // The task is an anonymous object that extends TimerTask and overrides its run method.
        // Inside the run method, it calls the onTick function passed as a parameter to startTimer.
        timer?.schedule(object : TimerTask() {
            override fun run() {
                onTick(this)
            }
        }, 0, 1000) // The task is scheduled to run immediately (after 0 milliseconds delay) and repeat every 1000 milliseconds (1 second).
    }

    // Function to stop the timer.
    fun stopTimer() {
        // Call the cancel method on the timer instance to stop it.
        // The timer instance is then set to null.
        timer?.cancel()
    }
}


/**
 * Shuffles the keys of a given board map.
 *
 * @param board The board map to shuffle.
 * @param seed The seed for the random number generator. This is used to ensure that the shuffling is deterministic.
 * @return A list of shuffled keys from the board map.
 */
fun shuffleBoard(board: Map<String, String>, seed: String): List<String> {
    // Convert the seed to a hash code. This is used to initialize the random number generator.
    val random = Random(seed.hashCode())
    // Shuffle the keys of the board map using the random number generator and return the shuffled list.
    return board.keys.shuffled(random)
}


