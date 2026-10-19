package com.example.todocompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.fetchTodos()
                _todos.value = result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

}
