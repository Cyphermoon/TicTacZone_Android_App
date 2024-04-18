package com.cyphermoon.tictaczone.presentation.game_flow.composables

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyphermoon.tictaczone.presentation.game_flow.utils.shuffleBoard
import com.cyphermoon.tictaczone.ui.theme.LightSecondary

@Composable
fun TicTacToeBoard(
    label: String,
    board: Map<String, String>,
    handleCellClicked: (String) -> Unit,
    currentMarker: String,
    distortedGhost: Boolean,
    className: String,
    player1Id: String?,
    player2Id: String?,
    distortedMode: Boolean = false,
    disableBoard: Boolean? = false
) {
    var positions by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(key1 = Unit) {
        positions = if (distortedMode) {
            shuffleBoard(board, "${player1Id}_${player2Id}")
        } else {
            board.keys.toList()
        }

    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(LightSecondary)
            .clip(RoundedCornerShape(25.dp))
            .padding(16.dp)
            .fillMaxWidth()
    ) {


        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))

        BoxWithConstraints(modifier = Modifier.aspectRatio(1f)) {
            val cellSize = (constraints.maxWidth / 3)

            Canvas(
                modifier = Modifier
                    .fillMaxSize() // Fill the maximum available space
                    .aspectRatio(1f) // Maintain an aspect ratio of 1:1 for the Canvas
                    .pointerInput(disableBoard) { // Enable pointer input
                        detectTapGestures { // Detect tap gestures
                            Log.d("TicTacToeBoard", "disableBoard: $disableBoard") // Add this line
                            if(disableBoard == false){
                                // Calculate the x and y coordinates of the tap
                                val x = (3 * it.x.toInt() / size.width)
                                val y = (3 * it.y.toInt() / size.height)
                                // Calculate the index of the cell that was tapped
                                val index = if(distortedMode) y * 3 + x else y * 3 + x + 1
                                // Get the position of the cell in the shuffled positions list
                                val position = if (distortedMode) positions[index] else index.toString()
                                // Call the handleCellClicked function with the position
                                handleCellClicked(position)
                            }

                        }
                    }
            ) {
                drawField() // Draw the tic-tac-toe field

                // Iterate over the positions list with the index
                positions.forEachIndexed { index, position ->
                    // Get the player who marked this position
                    val player = board[position]

                    // Calculate the x and y coordinates of the cell
                    val x = index % 3
                    val y = index / 3

                    // Calculate the center of the cell
                    val center = Offset(
                        x = x * size.width / 3f + size.width / 6f,
                        y = y * size.height / 3f + size.height / 6f
                    )

                    // Draw the player's mark in the cell
                    if (player != null) {
                        when (player.lowercase()) {
                            "x" -> drawX(center = center)
                            "o" -> drawO(center = center)
                        }
                    }

                    // If distortedGhost is true, draw the position number
                    if (distortedGhost) {
                        drawIntoCanvas { canvas ->
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.GRAY
                                alpha = 128 // semi-transparent
                                textSize = 40f
                            }
                            // Draw the position number in the cell
                            canvas.nativeCanvas.drawText(
                                position,
                                center.x,
                                center.y,
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun DrawScope.drawField() {
    val strokeWidth = 3.dp.toPx()

    // 1st vertical line
    drawLine(
        color = Color.Black,
        start = Offset(x = size.width / 3, y = 0f),
        end = Offset(x = size.width / 3, y = size.height),
        strokeWidth = strokeWidth
    )

    // 2nd vertical line
    drawLine(
        color = Color.Black,
        start = Offset(x = size.width * 2 / 3, y = 0f),
        end = Offset(x = size.width * 2 / 3, y = size.height),
        strokeWidth = strokeWidth
    )

    // 1st horizontal line
    drawLine(
        color = Color.Black,
        start = Offset(x = 0f, y = size.height / 3),
        end = Offset(x = size.width, y = size.height / 3),
        strokeWidth = strokeWidth
    )

    // 2nd horizontal line
    drawLine(
        color = Color.Black,
        start = Offset(x = 0f, y = size.height * 2 / 3),
        end = Offset(x = size.width, y = size.height * 2 / 3),
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawX(center: Offset) {
    // Define the size of the 'X'. This is set to 50.dp (density-independent pixels),
    // which is converted to pixels using the `toPx()` function.
    val size = Size(50.dp.toPx(), 50.dp.toPx())

    // Define the stroke width for the lines. This is set to 3.dp, which is converted to pixels.
    val strokeWidth = 3.dp.toPx()

    // Draw the first line of the 'X'. The start and end points are calculated based on the center of the cell.
    // The line is drawn from the top-left to the bottom-right of the cell.
    drawLine(
        color = Color.Black, // The color of the line is set to black.
        start = Offset(
            x = center.x - size.width / 2f, // The start x-coordinate is the center x-coordinate minus half the width of the cell.
            y = center.y - size.height / 2f // The start y-coordinate is the center y-coordinate minus half the height of the cell.
        ),
        end = Offset(
            x = center.x + size.width / 2f, // The end x-coordinate is the center x-coordinate plus half the width of the cell.
            y = center.y + size.height / 2f // The end y-coordinate is the center y-coordinate plus half the height of the cell.
        ),
        strokeWidth = strokeWidth, // The width of the line is set to the defined stroke width.
        cap = StrokeCap.Round // The ends of the line are rounded.
    )

    // Draw the second line of the 'X', similar to the first line but in the opposite direction.
    // The line is drawn from the bottom-left to the top-right of the cell.
    drawLine(
        color = Color.Black,
        start = Offset(
            x = center.x - size.width / 2f,
            y = center.y + size.height / 2f
        ),
        end = Offset(
            x = center.x + size.width / 2f,
            y = center.y - size.height / 2f
        ),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawO(center: Offset) {
    // Define the size of the 'O'. This is set to 50.dp (density-independent pixels),
    // which is converted to pixels using the `toPx()` function.
    val size = Size(50.dp.toPx(), 50.dp.toPx())

    // Define the stroke width for the circle. This is set to 3.dp, which is converted to pixels.
    val strokeWidth = 3.dp.toPx()

    // Draw the 'O' as a circle. The center of the circle is the center of the cell.
    drawCircle(
        color = Color.Black, // The color of the circle is set to black.
        center = center, // The center of the circle is the center of the cell (`center.x` and `center.y`).
        radius = size.width / 2f, // The radius of the circle is half the width of the cell.
        style = Stroke(width = strokeWidth) // The width of the circle's stroke is set to the defined stroke width.
    )
}


@Preview(showBackground = true)
@Composable
fun TicTacToeBoardPreview() {
    val board = mapOf(
        "1" to "X",
        "2" to "O",
        "3" to "O",
        "4" to "X",
        "5" to "O",
        "6" to "",
        "7" to "X",
        "8" to "O",
        "9" to ""
    )

    TicTacToeBoard(
        label = "Tic Tac Toe",
        board = board,
        handleCellClicked = {},
        currentMarker = "X",
        distortedGhost = false,
        className = "TicTacToeBoard",
        player1Id = "player1",
        player2Id = "player2",
        distortedMode = false
    )
}