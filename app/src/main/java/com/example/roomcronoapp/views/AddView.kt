package com.example.roomcronoapp.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomcronoapp.R
import com.example.roomcronoapp.component.CircleButton
import com.example.roomcronoapp.component.MainIconButton
import com.example.roomcronoapp.component.MainTextField
import com.example.roomcronoapp.component.MainTitle
import com.example.roomcronoapp.component.formatTiempo
import com.example.roomcronoapp.model.Cronos
import com.example.roomcronoapp.viewModels.CronometroViewModel
import com.example.roomcronoapp.viewModels.CronosViewModel

//Agregamos como parametro el CronometroVM
// que utilizaremos en el composable de contenido
//Agregar el VM Cronos para guardar en room
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddView(
    navController: NavController,
    cronometroVM: CronometroViewModel,
    cronosVM: CronosViewModel
){
    Scaffold(
        topBar = {
            //clase de material que crea una barra en la parte superior
            CenterAlignedTopAppBar(
                title = { MainTitle(title = "Add Crono") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    //definido en la clase Buttons.kt
                    MainIconButton(icon = Icons.Default.ArrowBack) {
                        navController.popBackStack()
                    }
                }
            )
        }
    ) {
        ContentAddView(it, navController,cronometroVM, cronosVM )
    }
}

//agregamos como parametro el CronometroVM
//agregar el VM cronos que guarda en room
@Composable
fun ContentAddView(
    it: PaddingValues,
    navController: NavController,
    cronometroVM: CronometroViewModel,
    cronosVM: CronosViewModel
){
    //creamos una variable para manejar el estado
    val state = cronometroVM.state
    //lanzar el cronometro en una corrutina
    LaunchedEffect(state.cronometroActivo ){
        cronometroVM.cronos()//ejecutar el cronometro en el VM
    }
    Column(
        modifier = Modifier
            .padding(it)
            .padding(top = 30.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatTiempo(tiempo = cronometroVM.tiempo),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 16.dp)) {
            //iniciar
            CircleButton(icon = painterResource(id = R.drawable.play), enabled = !state.cronometroActivo) {
                cronometroVM.iniciar()
            }
            //pausar
            CircleButton(icon = painterResource(id = R.drawable.pause), enabled = state.cronometroActivo) {
                cronometroVM.pausar()
            }
            //detener
            CircleButton(icon = painterResource(id = R.drawable.stop), enabled = !state.cronometroActivo) {
                cronometroVM.detener()
            }
            //mostrar TextField
            CircleButton(icon = painterResource(id = R.drawable.guardar), enabled = state.showSaveButton) {
                cronometroVM.showTextField()
            }
        }
        //Agregar los elementos necesarios para guardar
        if(state.showTextField){
            MainTextField(value = state.title,
                onValueChange = {cronometroVM.onValue(it)}, label = "titulo")
            
            Button(onClick = {
                //invocamos a la funcion addCrono del ViewModel Cronos
                cronosVM.addCrono(
                    //creamos un objeto de nuestro DataClass Cronos
                    Cronos(
                        title = state.title,
                        //tomamos el tiempo ejecutado por el VM Cronometro
                        crono = cronometroVM.tiempo
                    )
                )
                //Detener el cronometro
                cronometroVM.detener()
                //Regresar a la pantalla Home
                navController.popBackStack()
            }) {
                Text(text = "Guardar")
            }
        }
    }

}