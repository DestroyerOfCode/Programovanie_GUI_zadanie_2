package quiz

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
                        answerComposable(answer,
                            { if (selectedButton.value == index) Color.Blue else Color.Black },
                            {},
                            {
                                RadioButton(selected = index == selectedButton.value,
                                    modifier = Modifier,
                                    onClick = { selectedButton.value = index })
                            })
                    }
                }
            }
            answerButton(questionIndex, question, selectedButton)
        }
    }
    @Composable
    private fun quizComposable(
        questionIndex: MutableState<Int>, question: Question, composeAnswer: @Composable () -> Unit
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
        icon: @Composable () -> Unit = {},
        radioButton: @Composable () -> Unit,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = Dp(50f))) {
            icon()
            radioButton()
            Text(
                modifier = Modifier.padding(top = Dp(12f)), color = color(), text = answer.name
            )
        }
    }
    @Composable
    private fun answerButton(
        questionIndex: MutableState<Int>, question: Question, selectedButton: MutableState<Int>
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
            }, enabled = selectedButton.value != -1, modifier = Modifier.padding(start = Dp(65f))
        ) {
            Text(text = "answer", textAlign = TextAlign.Center)
        }
    }
    @Composable
    private fun endGame(questionIndex: MutableState<Int>) {
        Column {
            Text(
                text = "Congratulations. You scored ${gameboardViewModel.quiz.value.score} out of $QUESTIONS",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = Dp(50f))
            )
            gameboardViewModel.quiz.value.questions.forEachIndexed { index, question ->
                quizComposable(
                    mutableStateOf(index), question
                ) {
                    question.answers.forEachIndexed { indexAnswer, answer ->
                        answerComposable(
                            answer, answerColor(index, indexAnswer, answer), answerIcon(index, indexAnswer, answer)
                        ) {
                            RadioButton(selected = indexAnswer == gameboardViewModel.selectedAnswersIndex[index],
                                modifier = Modifier,
                                enabled = false,
                                onClick = {})
                        }
                    }
                }
            }
            Button(
                onClick = {
                    questionIndex.value = 0
                    gameboardViewModel.quiz.value.score = 0
                    gameboardViewModel.nullifyAnswers()
                },
                modifier = Modifier.padding(start = Dp(50f))
            ) {
                Text(text = "Do you want to play again?", textAlign = TextAlign.Center, modifier = Modifier.padding(start = Dp(50f)))
            }
        }
    }

    private fun answerIcon(index: Int, indexAnswer: Int, answer: Answer): @Composable () -> Unit = {
        val icon = if (gameboardViewModel.selectedAnswersIndex[index] == indexAnswer && answer.isCorrect) {
            Icon(Icons.Filled.Check, "icon correct", modifier = Modifier.padding(start = Dp(10f), top = Dp(10f)))
        } else if (gameboardViewModel.selectedAnswersIndex[index] != indexAnswer && answer.isCorrect) {
            Icon(Icons.Filled.Check, "icon correct", modifier = Modifier.padding(start = Dp(10f), top = Dp(10f)))
        } else if (gameboardViewModel.selectedAnswersIndex[index] != indexAnswer && !answer.isCorrect) {
            null
        } else {
            Icon(Icons.Filled.Close, "icon wrong", modifier = Modifier.padding(start = Dp(10f), top = Dp(10f)))
        }
        icon
    }

    private fun answerColor(
        index: Int, indexAnswer: Int, answer: Answer
    ): () -> Color = {
        val whatColor: Color = if (gameboardViewModel.selectedAnswersIndex[index] == indexAnswer && answer.isCorrect) {
            Color.Green
        } else if (gameboardViewModel.selectedAnswersIndex[index] != indexAnswer && answer.isCorrect) {
            Color.Green
        } else if (gameboardViewModel.selectedAnswersIndex[index] != indexAnswer && !answer.isCorrect) {
            Color.Black
        } else {
            Color.Red
        }
        whatColor
    }
}