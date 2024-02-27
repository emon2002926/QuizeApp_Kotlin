import android.util.Log
import com.example.quizeapp.QuizData
import org.json.JSONArray
import org.json.JSONException
import java.nio.charset.StandardCharsets

    object ApiResponseParser {
    fun parseResponse(response: String): List<QuizData> {
        val tempList = mutableListOf<QuizData>()
        try {
            val data = JSONArray(response)
            for (i in 0 until data.length()) {
                val row = data.getJSONObject(i)
                val question = convertToUTF8(row.getString("question"))
                val option1 = convertToUTF8(row.getString("option1"))
                val option2 = convertToUTF8(row.getString("option2"))
                val option3 = convertToUTF8(row.getString("option3"))
                val option4 = convertToUTF8(row.getString("option4"))
                val answer = row.getInt("answer")
                val questionItem=QuizData(question, option1,option2,option3,option4,answer)

                tempList.add(questionItem)
                Log.d("question",question)
                Log.d("question",answer.toString())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            // Handle JSON parsing exception here
        }
        return tempList.toList()
    }

    private fun convertToUTF8(inputString: String): String {
        return String(inputString.toByteArray(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)
    }
}
