package com.example.todocompose.ui.theme.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todocompose.TodoViewModel
import com.example.todocompose.data.model.Todo

@Composable
fun TodoScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var editingTodo by remember { mutableStateOf<Todo?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTodos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mis Tareas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
        )

        TextField(
            value = titleInput,
            onValueChange = { titleInput = it },
            label = { Text("Titulo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = descriptionInput,
            onValueChange = { descriptionInput = it },
            label = { Text("Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val currentEditing = editingTodo
                    if (currentEditing == null) {
                        viewModel.addTodo(titleInput, descriptionInput)
                    } else {
                        viewModel.updateTodo(
                            currentEditing.copy(
                                title = titleInput,
                                description = descriptionInput
                            )
                        )
                        editingTodo = null
                    }
                    titleInput = ""
                    descriptionInput = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (editingTodo == null) "Agregar" else "Guardar cambios")
            }

            if (editingTodo != null) {
                OutlinedButton(
                    onClick = {
                        editingTodo = null
                        titleInput = ""
                        descriptionInput = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            TodoList(
                todos = todos,
                onToggleDone = { todo ->
                    viewModel.toggleTodoDone(todo)
                },
                onEditClick = { todo ->
                    editingTodo = todo
                    titleInput = todo.title
                    descriptionInput = todo.description
                },
                onDeleteClick = { todo ->
                    if (editingTodo?.id == todo.id) {
                        editingTodo = null
                        titleInput = ""
                        descriptionInput = ""
                    }
                    viewModel.deleteTodo(todo)
                }
            )
        }
    }
}
