package vsu.cs.samsungstudentdb.screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import vsu.cs.samsungstudentdb.dto.GroupDto
import vsu.cs.samsungstudentdb.dto.StudentListDto
import vsu.cs.samsungstudentdb.dto.UpdateGroupDto
import vsu.cs.samsungstudentdb.util.CrudUtils


class EditGroupFragment : Fragment(), OnStudentItemClickListener {
    private lateinit var studentAPI: StudentAPI
    private lateinit var groupStudentsList: RecyclerView
    private lateinit var studentAdapter: StudentListAdapter
    private lateinit var editFacultyNameText: EditText
    private lateinit var editGroupNumText: EditText
    private var unmappedStudent = mutableMapOf<String, StudentListDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentAPI = StudentDBClient.getClient().create(StudentAPI::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_group, container, false)
        editFacultyNameText = view.findViewById(R.id.editFacultyNameText)
        editGroupNumText = view.findViewById(R.id.editGroupNumText)
        groupStudentsList = view.findViewById(R.id.groupStudentsList)
        groupStudentsList.layoutManager = LinearLayoutManager(requireContext())

        val floatingActionButton =
            view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            CrudUtils.showRadioButtonDialog(
                requireContext(),
                "Выберите студента",
                unmappedStudent.keys.toList()
            ) { selectedValue ->
                addStudentGroup(selectedValue)
                arguments?.getInt("group_id")?.let { getGroupsStudents(it) }
            }
        }


        val confirmEditBtn = view.findViewById<AppCompatButton>(R.id.confirmEditBtn)
        confirmEditBtn.setOnClickListener {
            arguments?.getInt("group_id")?.let { update(it) }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt("group_id")?.let { getGroupsStudents(it) }
        getStudents()
    }

    private fun getGroupsStudents(id: Int) {
        val call = studentAPI.getGroupStudents(id)

        call.enqueue(object : Callback<GroupDto> {
            override fun onResponse(
                call: Call<GroupDto>,
                response: Response<GroupDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    if (dataResponse != null) {
                        studentAdapter = StudentListAdapter(
                            requireContext(),
                            dataResponse.students,
                            this@EditGroupFragment
                        )
                        groupStudentsList.adapter = studentAdapter
                        editGroupNumText.setText("${dataResponse.group_number}")
                        editFacultyNameText.setText("${dataResponse.faculty_name}")
                    }

                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<GroupDto>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun getStudents() {
        unmappedStudent.clear()
        val call = studentAPI.getNoGroupStudents()

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
                        studentAdapter = StudentListAdapter(
                            requireContext(),
                            dataResponse,
                            this@EditGroupFragment
                        )
                        for (student in dataResponse) {
                            unmappedStudent["${student.second_name} ${student.first_name} ${student.third_name}"] =
                                student
                        }
                    }
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

    private fun update(id: Int) {
        val updGroup = UpdateGroupDto(
            editGroupNumText.text.toString().toInt(),
            editFacultyNameText.text.toString()
        )
        val call = studentAPI.updateGroup(id, updGroup)

        call.enqueue(object : Callback<UpdateGroupDto> {
            override fun onResponse(
                call: Call<UpdateGroupDto>,
                response: Response<UpdateGroupDto>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    getGroupsStudents(id)
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<UpdateGroupDto>, t: Throwable) {
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
                    getGroupsStudents(id)
                } else {
                    println("Не успешно")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    private fun deleteStudentFromGroup(student_id: Int, group_id: Int) {
        val call = studentAPI.deleteStudentFromGroup(student_id, group_id)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    showChosenItem("Студент удалён из группы")
                    getGroupsStudents(group_id)
                    getStudents()
                } else {
                    println("Не успешно")
                    showChosenItem("Ошибка! Студент не удалён из группы")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }


    private fun addStudentGroup(student: String) {

        val studentId = unmappedStudent[student]!!.id
        val call = studentAPI.addStudentInGroup(studentId, requireArguments().getInt("group_id"))

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    showChosenItem("Студент добавлен в группу")
                    getGroupsStudents(arguments!!.getInt("group_id"))
                    getStudents()
                } else {
                    showChosenItem("Ошибка")
                    println("Не успешно")
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
                getGroupsStudents(arguments!!.getInt("group_id"))
                alertDialog.dismiss()
            }
        })
    }

    override fun onDeleteClick(student: StudentListDto) {
        arguments?.let { deleteStudentFromGroup(student.id, it.getInt("group_id")) }
        getStudents()
    }

    override fun onItemClick(id: Int) {

    }


    private fun showChosenItem(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}