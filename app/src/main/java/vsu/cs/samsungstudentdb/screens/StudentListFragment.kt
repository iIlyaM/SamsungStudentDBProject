package vsu.cs.samsungstudentdb.screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.samsungstudentdb.R
import vsu.cs.samsungstudentdb.StudentDBClient
import vsu.cs.samsungstudentdb.adapters.OnStudentItemClickListener
import vsu.cs.samsungstudentdb.adapters.StudentListAdapter
import vsu.cs.samsungstudentdb.api.StudentAPI
import vsu.cs.samsungstudentdb.dto.EditStudentDto
import vsu.cs.samsungstudentdb.dto.StudentListDto

class StudentListFragment : Fragment(), OnStudentItemClickListener {
    private lateinit var studentAPI: StudentAPI
    private lateinit var studentsView: RecyclerView
    private lateinit var studAdapter: StudentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentAPI = StudentDBClient.getClient().create(StudentAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_list, container, false)
        studentsView = view.findViewById(R.id.students_list)
        studentsView.layoutManager = LinearLayoutManager(requireContext())
        val floatingActionButton = view.findViewById<AppCompatButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_studentListFragment_to_createStudentFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStudents(null, null)
        val searchByNameView = view.findViewById<SearchView>(R.id.searchByNameView)
        searchByNameView.setQueryHint("Поиск по фамилии");
        val searchByGroupNum = view.findViewById<SearchView>(R.id.searchByGroupNum)
        searchByGroupNum.setQueryHint("Поиск по номеру группы");
        searchByNameView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getStudents(null, newText)
                }

                return true
            }
        })

        searchByGroupNum.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    getStudents(newText, null)
                }

                return true
            }
        })
    }

    private fun getStudents(group_num: String?, second_name: String?) {
        var group_number: Int?
        group_number = if (group_num == null) {
            null
        } else {
            if (group_num.isEmpty()) {
                null
            } else {
                group_num.toInt()
            }
        }
        val call = studentAPI.getStudents(group_number, second_name)


        call.enqueue(object : Callback<List<StudentListDto>> {
            override fun onResponse(
                call: Call<List<StudentListDto>>,
                response: Response<List<StudentListDto>>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        studAdapter = StudentListAdapter(
                            requireContext(),
                            dataResponse,
                            this@StudentListFragment
                        )
                    }
                    studentsView.adapter = studAdapter

                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<List<StudentListDto>>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun updateStudent(id: Int, updStudentDto: EditStudentDto) {
        val call = studentAPI.updateStudent(id, updStudentDto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    showChosenItem("Данные обновлены")
                    getStudents(null, null)
                } else {
                    println("Не успешно")
                    showChosenItem("Ошибка")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun deleteStudent(id: Int) {
        val call = studentAPI.deleteStudent(id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    showChosenItem("Студент удалён")
                    getStudents(null, null)
                } else {
                    println("Не успешно")
                    showChosenItem("Ошибка! Студент не удалён")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    override fun onEditClick(student: StudentListDto) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.update_group_student, null)
        val editFirstName = dialogView.findViewById<AppCompatEditText>(R.id.updFirstName)
        val editSecondName = dialogView.findViewById<AppCompatEditText>(R.id.updSecondName)
        val editThirdName = dialogView.findViewById<AppCompatEditText>(R.id.updThirdName)
        val updBirthdayText = dialogView.findViewById<AppCompatEditText>(R.id.updBirthdayText)
        val updateStudentBtn = dialogView.findViewById<AppCompatButton>(R.id.updateStudentBtn)

        editFirstName?.setText(student.first_name)
        editSecondName?.setText(student.second_name)
        editThirdName?.setText(student.third_name)
        updBirthdayText?.setText(student.birthday)

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

        updateStudentBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val id = student.id
                val first_name = editFirstName?.text.toString()
                val second_name = editSecondName?.text.toString()
                val third_name = editThirdName?.text.toString()
                val birthday = updBirthdayText?.text.toString()
                updateStudent(id, EditStudentDto(first_name, second_name, third_name, birthday))
                getStudents(null, null)
                alertDialog.dismiss()
            }
        })
    }

    override fun onDeleteClick(student: StudentListDto) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(id: Int) {
        return
    }

    private fun showChosenItem(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}