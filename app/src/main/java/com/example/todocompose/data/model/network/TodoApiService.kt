package com.example.todocompose.data.model.network

import com.example.todocompose.data.model.Todo
import retrofit2.http.*

interface TodoApiService {

    @GET("to-do")
    suspend fun getTodos(): List<Todo>

}
