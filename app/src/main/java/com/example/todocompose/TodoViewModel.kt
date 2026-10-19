package com.example.todocompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.data.model.Todo
import com.example.todocompose.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val initialFallbackTodos = listOf(
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000001",
            title = "Pagar servicios",
            description = "Pagar la factura de luz, agua e internet antes del viernes.",
            done = false
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000002",
            title = "Llamar al médico",
            description = "Agendar cita para chequeo general.",
            done = false
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000003",
            title = "Limpiar escritorio",
            description = "Organizar papeles y limpiar el polvo.",
            done = true
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000004",
            title = "Revisar presupuesto",
            description = "Actualizar gastos del mes en la hoja de cálculo.",
            done = false
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000005",
            title = "Practicar inglés",
            description = "Ver un episodio de serie en inglés con subtítulos.",
            done = true
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000006",
            title = "Actualizar CV",
            description = "Agregar experiencia reciente y nuevos cursos.",
            done = false
        ),
        Todo(
            id = "1a2b3c4d-1111-aaaa-bbbb-000000000007",
            title = "Armar lista de compras",
            description = "Preparar lista para ir al mercado el sábado.",
            done = true
        )
    )

    fun loadTodos() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.fetchTodos()
                _todos.value = result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                if (_todos.value.isEmpty()) {
                    _todos.value = initialFallbackTodos
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return

        val newTodo = Todo(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            done = false
        )

        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.addTodo(newTodo)
                _todos.value += result
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                // Si la API responde error (ej. 404 en el servidor de prueba), mantenemos la tarea localmente para poder probar el CRUD completo
                _todos.value += newTodo
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleTodoDone(todo: Todo) {
        val updatedTodo = todo.copy(done = !todo.done)
        updateTodo(updatedTodo)
    }

    fun updateTodo(updatedTodo: Todo) {
        if (updatedTodo.title.isBlank()) return

        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.updateTodo(updatedTodo)
                _todos.value = _todos.value.map {
                    if (it.id == updatedTodo.id) result else it
                }
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = _todos.value.map {
                    if (it.id == updatedTodo.id) updatedTodo else it
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.deleteTodo(todo.id)
                _todos.value = _todos.value.filter { it.id != todo.id }
            } catch (e: Exception) {
                Log.e("TodoViewModel", e.message.toString())
                _todos.value = _todos.value.filter { it.id != todo.id }
            } finally {
                _loading.value = false
            }
        }
    }

}
