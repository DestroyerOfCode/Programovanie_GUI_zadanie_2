package quiz

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
                quizComposable(questionIndex, question) {
                    question.answers.forEachIndexed { index, answer ->
                        answerComposable(
                            answer,
                            { if (selectedButton.value == index) Color.Blue else Color.Black },
                            {
                                RadioButton(
                                    selected = index == selectedButton.value,
                                    modifier = Modifier.padding(start = Dp(50f)),
                                    onClick = { selectedButton.value = index })
                            }
                        )
                    }
                }
            }
            answerButton(questionIndex, question, selectedButton)
        }
    }
    @Composable
    private fun quizComposable(
        questionIndex: MutableState<Int>,
        question: Question,
        composeAnswer: @Composable () -> Unit
    ) {
        Text(
            text = "Question ${questionIndex.value + 1}/$QUESTIONS: ${question.name}",
            modifier = Modifier.padding(start = Dp(50f), top = Dp(50f), bottom = Dp(20f))
        )
        composeAnswer()
    }
    @Composable
    private fun answerComposable(
        answer: Answer,
        color: () -> Color,
        radioButton: @Composable () -> Unit
    ) {
        Row {
            radioButton()
            Text(
                text = answer.name,
                modifier = Modifier.padding(top = Dp(12f)),
                color = color()
            )
        }
    }
    @Composable
    private fun answerButton(
        questionIndex: MutableState<Int>,
        question: Question,
        selectedButton: MutableState<Int>
    ) {
        Button(
            onClick = {
                if (questionIndex.value >= QUESTIONS) {
                    return@Button
                }

                if (question.answers[selectedButton.value].isCorrect) {
                    gameboardViewModel.quiz.value.score++
                }
                gameboardViewModel.addAnswer(selectedButton.value)
                selectedButton.value = -1
                questionIndex.value++
            },
            enabled = selectedButton.value != -1,
            modifier = Modifier.padding(start = Dp(65f))
        ) {
            Text(text = "answer", textAlign = TextAlign.Center)
        }
    }
    @Composable
    private fun endGame(questionIndex: MutableState<Int>) {
        Column {
            Text(
                text = "Congratulations. You scored ${gameboardViewModel.quiz.value.score} out of $QUESTIONS",
                textAlign = TextAlign.Center
            )
            gameboardViewModel.quiz.value.questions.forEachIndexed { index, question ->
                quizComposable(
                    mutableStateOf(index),
                    question
                ) {
                    question.answers.forEachIndexed { indexAnswer, answer ->
                        answerComposable(answer, { if (answer.isCorrect) Color.Green else Color.Red }, {
                            RadioButton(
                                selected = indexAnswer == gameboardViewModel.selectedAnswersIndex[index],
                                modifier = Modifier.padding(start = Dp(50f)),
                                enabled = false,
                                onClick = {})
                        })
                    }
                }
            }
            Button(
                onClick = {
                    questionIndex.value = 0
                    gameboardViewModel.quiz.value.score = 0
                    gameboardViewModel.nullifyAnswers()
                },
            ) {
                Text(text = "Do you want to play again?", textAlign = TextAlign.Center)
            }
        }
    }
}