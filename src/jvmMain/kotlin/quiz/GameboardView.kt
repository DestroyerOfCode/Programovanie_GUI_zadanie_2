package quiz

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import quiz.GameboardViewModel.Companion.QUESTIONS

class GameboardView(private val gameboardViewModel: GameboardViewModel = GameboardViewModel()) {
    @Composable
    @Preview
    fun init() {
        val selectedButton: MutableState<Int> = remember { mutableStateOf(-1) }
        val questionIndex: MutableState<Int> = remember { mutableStateOf(0) }

        if (questionIndex.value >= QUESTIONS) {
            endGame(questionIndex)
            return
        }

        val question = gameboardViewModel.quiz.value.questions[questionIndex.value]
        Column(modifier = Modifier.fillMaxHeight()) {
            Column {
                Text(text = question.name)
                question.answers.forEachIndexed { index, answer ->
                    Row {
                        RadioButton(
                            selected = index == selectedButton.value,
                            onClick = { selectedButton.value = index })
                        Text(text = answer.name, color = if (selectedButton.value == index) Color.Green else Color.Black )
                    }
                }
            }
            answerButton(questionIndex, question, selectedButton)
        }
    }

    @Composable
    private fun answerButton(
        questionIndex: MutableState<Int>,
        question: Question,
        selectedButton: MutableState<Int>
    ) {
        Button(onClick = {
            if (questionIndex.value >= QUESTIONS) {
                return@Button
            }

            if (question.answers[selectedButton.value].isCorrect) {
                gameboardViewModel.quiz.value.score++
            }
            selectedButton.value = -1
            questionIndex.value++
        },
        enabled = selectedButton.value != -1
        ) {
            Text(text = "answer")
        }
    }
    @Composable
    private fun endGame(questionIndex: MutableState<Int>) {
        Column {
            Text(text = "Congratulations. You scored ${gameboardViewModel.quiz.value.score} out of $QUESTIONS")
            Button(onClick = {
                questionIndex.value = 0
                gameboardViewModel.quiz.value.score = 0
            }) {
                Text(text = "Do you want to play again?")
            }
        }
    }
}