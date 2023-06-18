package vsu.cs.samsungstudentdb.screens

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vsu.cs.samsungstudentdb.R
import vsu.cs.samsungstudentdb.StudentDBClient
import vsu.cs.samsungstudentdb.adapters.GroupListAdapter
import vsu.cs.samsungstudentdb.adapters.OnGroupItemClickListener
import vsu.cs.samsungstudentdb.api.StudentAPI
import vsu.cs.samsungstudentdb.dto.CreateGroupDto
import vsu.cs.samsungstudentdb.dto.EditStudentDto
import vsu.cs.samsungstudentdb.dto.GroupDto
import vsu.cs.samsungstudentdb.dto.GroupListDto
import java.util.concurrent.atomic.AtomicReference


class GroupsListFragment : Fragment(), OnGroupItemClickListener {

    private lateinit var studentAPI: StudentAPI
    private lateinit var groupsView: RecyclerView
    private lateinit var groupAdapter: GroupListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentAPI = StudentDBClient.getClient().create(StudentAPI::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups_list, container, false)
        groupsView = view.findViewById(R.id.groupsView)
        groupsView.layoutManager = LinearLayoutManager(requireContext())

        val floatingActionButton =
            view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            createGroup()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGroups()
    }

    private fun addGroup(newGroup: CreateGroupDto) {
        val call = studentAPI.addGroup(newGroup)


        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    val dataResponse = response.body()
                    println(dataResponse)
                    getGroups()
                    showChosenItem("Группа добавлена")
                } else {
                    println("Не успешно")
                    showChosenItem("Ошибка! Группа не была добавлена")
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
                        groupAdapter = GroupListAdapter(
                            requireContext(),
                            dataResponse,
                            this@GroupsListFragment
                        )
                    }
                    groupsView.adapter = groupAdapter

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


    private fun deleteGroupById(id: Int) {
        val call = studentAPI.deleteGroup(id)


        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Log.d("API Request successful", "Получили ${response.code()}")
                    showChosenItem("Группа удалена")
                    getGroups()
                } else {
                    println("Не успешно")
                    if (response.code() == 400) {
                        showChosenItem("Не удалось удалить группу. Причина: группа не пуста")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Ошибка")
                println(t)
            }
        })
    }

    override fun onEditClick(group: GroupListDto) {
        val bundle = Bundle()
        bundle.putInt("group_id", group.id)
        findNavController().navigate(
            R.id.action_groupsListFragment_to_editGroupFragment,
            bundle
        )
    }

    override fun onDeleteClick(group: GroupListDto) {
        deleteGroupById(group.id)
    }

    override fun onItemClick(id: Int) {
    }

    private fun createGroup() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.create_group, null)
        val groupNumEditText = dialogView.findViewById<AppCompatEditText>(R.id.groupNumEditText)
        val facultyNameEditText = dialogView.findViewById<AppCompatEditText>(R.id.facultyNameEditText)
        val createGroupBtn = dialogView.findViewById<AppCompatButton>(R.id.createGroupBtn)
        val cancelUpdateGroupBtn = dialogView.findViewById<AppCompatButton>(R.id.cancelUpdateGroupBtn)

        groupNumEditText?.setHint("Номер группы")
        facultyNameEditText?.setHint("Факультет")

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.show()

        createGroupBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val groupNum = groupNumEditText?.text.toString().toInt()
                val facultyName = facultyNameEditText?.text.toString()
                addGroup(CreateGroupDto(groupNum, facultyName))
                alertDialog.dismiss()
            }
        })

        cancelUpdateGroupBtn.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showToastNotification(message: String) {
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(requireContext(), message, duration)
        toast.show()
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 1500)
    }

    private fun showChosenItem(msg: String) {
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
    }
}