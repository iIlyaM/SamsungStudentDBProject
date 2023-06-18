package vsu.cs.samsungstudentdb.dto

import com.google.gson.annotations.SerializedName

data class StudentListDto(
    val id: Int,
    val first_name: String,
    val second_name: String,
    val third_name: String,
    val birthday: String,
    @SerializedName("group")
    val groupDto: GroupDto?

)
