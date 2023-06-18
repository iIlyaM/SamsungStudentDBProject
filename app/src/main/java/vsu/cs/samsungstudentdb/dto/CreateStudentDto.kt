package vsu.cs.samsungstudentdb.dto

data class CreateStudentDto(
    val first_name: String,
    val second_name: String,
    val third_name: String,
    val birthday: String,
    val group_id: Int?
)
