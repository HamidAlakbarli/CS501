package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HangmanGameScreen()
        }
    }
}

// ViewModel: Handles game state
class HangmanViewModel : ViewModel() {
    private val wordList = listOf(
            "HORSE" to "A strong and fast animal",
            "BURGER" to "A popular fast food item",
            "PROTEIN" to "Essential nutrient for muscle growth",
            "PIZZA" to "A delicious cheesy dish"
    )

    var wordToGuess by mutableStateOf("")
    var displayedWord by mutableStateOf("")
    var selectedLetters = mutableStateListOf<Char>()
    var wrongGuessesCount by mutableStateOf(0)
    val maxWrong = 6
    var gameOver by mutableStateOf(false)
    var isWon by mutableStateOf(false)
    var hintMessage by mutableStateOf("")
    var hintClicks by mutableStateOf(0)
    private var currentHint = ""

    init {
        startNewGame()
    }

    fun startNewGame() {
        val (newWord, hint) = wordList.random()
        wordToGuess = newWord
        currentHint = hint
        selectedLetters.clear()
        wrongGuessesCount = 0
        hintClicks = 0
        hintMessage = ""
        gameOver = false
        isWon = false
        updateDisplayedWord()
    }

    private fun updateDisplayedWord() {
        displayedWord = wordToGuess.map { letter ->
            if (letter in selectedLetters) letter else '_'
        }.joinToString(" ")
    }

    fun useHint() {
        if (gameOver) return

        if (hintClicks == 0) {
            hintMessage = "Hint: $currentHint"
            hintClicks++
        } else if (hintClicks == 1 && wrongGuessesCount < maxWrong - 1) {
            val remainingWrong = ('A'..'Z').filter { it !in wordToGuess && it !in selectedLetters }
            val toDisableCount = remainingWrong.size / 2
            remainingWrong.shuffled().take(toDisableCount).forEach { selectedLetters.add(it) }
            hintMessage = "Some wrong letters disabled!"
            wrongGuessesCount++
            hintClicks++
        } else if (hintClicks == 2 && wrongGuessesCount < maxWrong - 1) {
            listOf('A', 'E', 'I', 'O', 'U').filter { it in wordToGuess }.forEach { selectedLetters.add(it) }
            updateDisplayedWord()
            hintMessage = "All vowels revealed!"
            wrongGuessesCount++
            hintClicks++
        } else {
            hintMessage = "Hint not available!"
        }
    }

    fun guessLetter(letter: Char) {
        if (gameOver || letter in selectedLetters) return
        selectedLetters.add(letter)
        if (letter in wordToGuess) {
            updateDisplayedWord()
            if (wordToGuess.all { it in selectedLetters }) {
                gameOver = true
                isWon = true
            }
        } else {
            wrongGuessesCount++
            if (wrongGuessesCount >= maxWrong) {
                gameOver = true
                isWon = false
            }
        }
    }
}

@Composable
fun HangmanGameScreen(viewModel: HangmanViewModel = viewModel()) {
    val orientation = LocalConfiguration.current.orientation
    val context = LocalContext.current

    LaunchedEffect(viewModel.gameOver) {
        if (viewModel.gameOver) {
            val msg = if (viewModel.isWon)
                "You Won! The word was ${viewModel.wordToGuess}."
            else
                "Game Over! The word was ${viewModel.wordToGuess}."
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "CHOOSE A LETTER",
                    fontSize = 20.sp, // Bigger size
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                LetterGrid(viewModel)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.useHint() },
                    enabled = viewModel.hintClicks < 3
                ) { Text("Hint") }

                if (viewModel.hintMessage.isNotEmpty()) {
                    Text(
                        text = viewModel.hintMessage,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HangmanImage(viewModel.wrongGuessesCount)
                Text(
                    viewModel.displayedWord,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.gameOver) {
                    Text(
                        text = if (viewModel.isWon) " You Won! " else " Game Over️",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.isWon) Color.Green else Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                NewGameButton(viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HangmanImage(viewModel.wrongGuessesCount)
            Text(viewModel.displayedWord, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "CHOOSE A LETTER",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            LetterGrid(viewModel)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.useHint() },
                enabled = viewModel.hintClicks < 3
            ) { Text("Hint") }

            if (viewModel.hintMessage.isNotEmpty()) {
                Text(
                    text = viewModel.hintMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            NewGameButton(viewModel)
        }
    }
}

@Composable
fun HangmanImage(wrongGuesses: Int) {
    val imageRes = when (wrongGuesses) {
        0 -> R.drawable.hangman_0
        1 -> R.drawable.hangman_1
        2 -> R.drawable.hangman_2
        3 -> R.drawable.hangman_3
        4 -> R.drawable.hangman_4
        5 -> R.drawable.hangman_5
        else -> R.drawable.hangman_6
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Hangman Stage $wrongGuesses",
        modifier = Modifier.size(200.dp)
    )
}
@Composable
fun LetterGrid(viewModel: HangmanViewModel) {
    Column {
        for (row in ('A'..'Z').chunked(7)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (letter in row) {
                    Button(
                        onClick = { viewModel.guessLetter(letter) },
                        enabled = letter !in viewModel.selectedLetters,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NewGameButton(viewModel: HangmanViewModel) {
    Button(onClick = { viewModel.startNewGame() }) { Text("New Game") }
}

//got help from Youtube, ChatGpt and Google
