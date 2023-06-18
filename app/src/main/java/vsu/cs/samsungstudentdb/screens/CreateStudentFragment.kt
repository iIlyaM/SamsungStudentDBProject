package vsu.cs.samsungstudentdb.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.samsungstudentdb.R
import vsu.cs.samsungstudentdb.StudentDBClient
import vsu.cs.samsungstudentdb.adapters.GroupListAdapter
import vsu.cs.samsungstudentdb.api.StudentAPI
import vsu.cs.samsungstudentdb.dto.CreateStudentDto
import vsu.cs.samsungstudentdb.dto.GroupListDto
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class CreateStudentFragment : Fragment() {

    private lateinit var studentAPI: StudentAPI
    private lateinit var groupTextView: AutoCompleteTextView
    private var groups = mutableMapOf<String, Int>()
    private var groupsArray = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentAPI = StudentDBClient.getClient().create(StudentAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_student, container, false)
        val createStudentBtn = view.findViewById<AppCompatButton>(R.id.createStudentBtn)
        createStudentBtn.setOnClickListener {
            createStudent(view)
        }

        groupTextView = view.findViewById(R.id.groupAutoCompleteTextView)

        groupTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGroups()
    }

    private fun createStudent(view: View) {
        val firstNameView = view.findViewById<EditText>(R.id.firstNameView)
        val secondNameView = view.findViewById<EditText>(R.id.secondNameView)
        val thirdNameView = view.findViewById<EditText>(R.id.thirdNameView)
        val birthDayView = view.findViewById<EditText>(R.id.birthDayView)
        val datePattern = "^\\d{2}\\.\\d{2}\\.\\d{4}\$".toRegex()
        if (firstNameView.text.toString().isEmpty() || secondNameView.text.toString()
                .isEmpty() || thirdNameView.text.toString()
                .isEmpty() || birthDayView.text.toString().isEmpty()
        ) {
            firstNameView.error = "Поле должно быть обязательно заполнено"
            secondNameView.error = "Поле должно быть обязательно заполнено"
            thirdNameView.error = "Поле должно быть обязательно заполнено"
            birthDayView.error = "Поле должно быть обязательно заполнено"
            return
        }

        if (!birthDayView.text.toString().matches(datePattern)) {
            birthDayView.error = "Введите дату в формате ДД.ММ.ГГГГ"
            return
        }
        val group = groupTextView.text.toString()
        var student: CreateStudentDto
        if (group.isEmpty()) {
            student = CreateStudentDto(
                firstNameView.text.toString(),
                secondNameView.text.toString(),
                thirdNameView.text.toString(),
                birthDayView.text.toString(),
                null
            )
        } else {
            student = CreateStudentDto(
                firstNameView.text.toString(),
                secondNameView.text.toString(),
                thirdNameView.text.toString(),
                birthDayView.text.toString(),
                groups[group]
            )
        }

        val call = studentAPI.addStudent(student)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request Successful", "${response.code()}")
                    showChosenItem("Новый студент добавлен")
                    firstNameView.text.clear()
                    secondNameView.text.clear()
                    thirdNameView.text.clear()
                    birthDayView.text.clear()
                    groupTextView.text.clear()
                } else {
                    println("Не успешно, ошибка = ${response.code()}")
                    showChosenItem("Ошибка! Студент не был добавлен")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun getGroups() {
        val call = studentAPI.getGroups()

        call.enqueue(object : Callback<List<GroupListDto>> {
            override fun onResponse(
                call: Call<List<GroupListDto>>,
                response: Response<List<GroupListDto>>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        for (group in dataResponse) {
                            val string = "${group.faculty_name}, №: ${group.group_number}"
                            groupsArray.add(string)
                            groups[string] = group.id
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, groupsArray)
                        groupTextView.setAdapter(adapter)
                    }
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<List<GroupListDto>>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun showChosenItem(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }

}
