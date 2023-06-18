package vsu.cs.samsungstudentdb.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import vsu.cs.samsungstudentdb.dto.CreateGroupDto
import vsu.cs.samsungstudentdb.dto.CreateStudentDto
import vsu.cs.samsungstudentdb.dto.EditStudentDto
import vsu.cs.samsungstudentdb.dto.GroupDto
import vsu.cs.samsungstudentdb.dto.GroupListDto
import vsu.cs.samsungstudentdb.dto.StudentListDto
import vsu.cs.samsungstudentdb.dto.UpdateGroupDto

interface StudentAPI {

    @Headers("Content-Type: application/json")
    @GET("students/students")
    fun getStudents(
        @Query("group_number") group_number: Int?,
        @Query("second_name") second_name: String?,
    ): Call<List<StudentListDto>>

    @Headers("Content-Type: application/json")
    @POST("students/new_student")
    fun addStudent(
        @Body student: CreateStudentDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @POST("students/group/new_group")
    fun addGroup(
        @Body group: CreateGroupDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @PUT("/students/{student_id}")
    fun updateStudent(
        @Path("student_id") student_id: Int,
        @Body student: EditStudentDto
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("students/group/groups")
    fun getGroups(): Call<List<GroupListDto>>

    @Headers("Content-Type: application/json")
    @GET("students/students/no_group_students")
    fun getNoGroupStudents(): Call<List<StudentListDto>>

    @Headers("Content-Type: application/json")
    @GET("students/group/{group_id}")
    fun getGroupStudents(
        @Path("group_id") group_id: Int
    ): Call<GroupDto>

    @Headers("Content-Type: application/json")
    @PUT("students/{student_id}/{group_id}")
    fun deleteStudentFromGroup(
        @Path("student_id") student_id: Int,
        @Path("group_id") group_id: Int
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("students/group/{group_id}")
    fun deleteGroup(
        @Path("group_id") group_id: Int
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @DELETE("students/{student_id}")
    fun deleteStudent(
        @Path("student_id") student_id: Int
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @PUT("students/group/{group_id}/")
    fun updateGroup(
        @Path("group_id") group_id: Int,
        @Body updateGroupDto: UpdateGroupDto
    ): Call<UpdateGroupDto>

    @Headers("Content-Type: application/json")
    @PUT("students/group/new_student/{group_id}/{student_id}/")
    fun addStudentInGroup(
        @Path("student_id") student_id: Int,
        @Path("group_id") group_id: Int,
    ): Call<Void>

}