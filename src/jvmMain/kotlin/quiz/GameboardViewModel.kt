package quiz

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import java.io.File

class GameboardViewModel  {
    var quiz: MutableState<Quiz>
        private set

    companion object {
        const val QUESTIONS = 2
    }
    init {
        val gson = Gson()
        val questions = File("src/jvmMain/resources/questions.json").readText()
        quiz = mutableStateOf(gson.fromJson(questions, Quiz::class.java))
    }
}