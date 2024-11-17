package com.example.roomcronoapp.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomcronoapp.repository.CronosRepository
import com.example.roomcronoapp.state.CronoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

//hilt nos permite inyectar constructores en un viewmodel
//inyectamos un repository para interactuar con la base de datos
@HiltViewModel
class CronometroViewModel @Inject constructor(private val repository: CronosRepository) : ViewModel() {

    var state by mutableStateOf(CronoState())
        private set

    //Job permite ejecutar un procesos que se puede iniciar, pausar y detener
    var cronoJob by mutableStateOf<Job?>(null)
        private set

    var tiempo by mutableStateOf(0L)
        //0L representa un Long
        private set

    fun onValue(value: String) {
        //lo usaremos para cambiar el valor en un textfield
        state = state.copy(title = value)
    }

    //funciones para manipular el cronometro
    //modificando el estado
    fun iniciar(){
        state = state.copy(
            cronometroActivo = true
        )
    }

    fun pausar(){
        state = state.copy(
            cronometroActivo = false,
            showSaveButton = true
        )
    }

    fun detener(){
        //cancelar el proceso completo
        cronoJob?.cancel()
        //reiniciar el cronometro
        tiempo = 0

        state = state.copy(
            cronometroActivo = false,
            showSaveButton = false,
            showTextField = false,
            title = ""
        )
    }

    fun showTextField(){
        state = state.copy(
            showTextField = true
        )
    }

    //ejecuta el cronometro
    fun cronos(){
        if(state.cronometroActivo){
            cronoJob?.cancel()
            //ejecutamos una corrutina para incrementar el tiempo
            //esto hace la funcion del cronometro
            cronoJob = viewModelScope.launch {
                while (true){
                    delay(1000)
                    tiempo += 1000
                }
            }
        }else{
            cronoJob?.cancel()
        }
    }

    fun getCronoById(id:Long){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCronoById(id).collect { item ->
                tiempo = item.crono
                state = state.copy(title = item.title)
            }
        }
    }

}


//
////funcion para detener el job
//fun det(){
//    cronoJob?.cancel()
//}