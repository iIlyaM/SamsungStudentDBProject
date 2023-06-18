package vsu.cs.samsungstudentdb.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vsu.cs.samsungstudentdb.R
import vsu.cs.samsungstudentdb.dto.StudentListDto

interface OnStudentItemClickListener {
    fun onEditClick(student: StudentListDto)
    fun onDeleteClick(student: StudentListDto)
    fun onItemClick(id: Int)
}

class StudentListAdapter(
    var context: Context,
    var students: List<StudentListDto>,
    val studentListener: OnStudentItemClickListener
):
    RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {
    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameView: TextView
        val facultyView: TextView
        val groupNum: TextView
        val birthdayView: TextView
        var settingsBtn: ImageView
        var deleteBtn: ImageView

        init {
            itemView.setOnClickListener {
                studentListener.onItemClick(students[adapterPosition].id)
            }
            fullNameView = itemView.findViewById(R.id.fullNameView)
            facultyView = itemView.findViewById(R.id.facultyView)
            groupNum = itemView.findViewById(R.id.groupNum)
            birthdayView = itemView.findViewById(R.id.birthdayView)
            settingsBtn = itemView.findViewById(R.id.icUserEditView)
            deleteBtn = itemView.findViewById(R.id.icUserDeleteView)

            settingsBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    studentListener.onEditClick(students[position])
                }
            }

            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    studentListener.onDeleteClick(students[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return StudentViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.student_list_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return students.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.fullNameView.text = "${student.second_name} ${student.first_name} ${student.third_name}"
        holder.facultyView.text = student.groupDto?.faculty_name
        holder.groupNum.text = "Группа: ${student.groupDto?.group_number}"
        holder.birthdayView.text = "Д.р: ${student.birthday}"
    }
}