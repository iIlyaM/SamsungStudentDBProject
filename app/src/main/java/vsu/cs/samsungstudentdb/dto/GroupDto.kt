package vsu.cs.samsungstudentdb.dto

data class GroupDto(
    val group_number: String?,
    val faculty_name: String?,
    val students_amount: Int,
    val students: List<StudentListDto>
)
