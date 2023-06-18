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
import vsu.cs.samsungstudentdb.dto.GroupListDto

interface OnGroupItemClickListener {
    fun onEditClick(group: GroupListDto)
    fun onDeleteClick(group: GroupListDto)
    fun onItemClick(id: Int)
}

class GroupListAdapter(
    var context: Context,
    var groups: List<GroupListDto>,
    val groupListener: OnGroupItemClickListener
):
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {
    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val facultyNameView: TextView
        val groupNum: TextView
        val studentsCountView: TextView
        var settingsBtn: ImageView
        var deleteBtn: ImageView

        init {
            itemView.setOnClickListener {
                groupListener.onItemClick(groups[adapterPosition].id)
            }
            facultyNameView = itemView.findViewById(R.id.facultyNameView)
            groupNum = itemView.findViewById(R.id.groupNumView)
            studentsCountView = itemView.findViewById(R.id.studentsCountView)
            settingsBtn = itemView.findViewById(R.id.editGroupView)
            deleteBtn = itemView.findViewById(R.id.deleteGroupView)

            settingsBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    groupListener.onEditClick(groups[position])
                }
            }

            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    groupListener.onDeleteClick(groups[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.group_list_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.facultyNameView.text = group.faculty_name
        holder.groupNum.text = "Группа: ${group.group_number}"
        holder.studentsCountView.text = "Студентов: ${group.students_amount}"
    }
}