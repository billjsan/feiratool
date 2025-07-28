package com.recife.bill.feiratool.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.recife.bill.feiratool.view.ui.components.AddEntryForm
import com.recife.bill.feiratool.view.ui.theme.FeiraToolTheme
import com.recife.bill.feiratool.viewmodel.AirPowerViewModelProvider

class WidgetAddItemActivity : ComponentActivity() {

    private val mainViewModel = AirPowerViewModelProvider.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pega o ID da lista que o widget nos enviou
        val listId = intent.getStringExtra("LIST_ID_EXTRA")

        // Se o listId não for encontrado, fecha a activity.
        if (listId == null) {
            finish()
            return
        }

        setContent {
            FeiraToolTheme {
                // Usamos o seu formulário já existente!
                AddEntryForm(
                    mainViewModel = mainViewModel,
                    listId = listId,
                    // O NavController aqui não é usado, então podemos passar um vazio
                    // ou adaptar o formulário para receber um lambda de "onSave".
                    navController = rememberNavController(),
                    // Adicionamos um lambda para ser executado após salvar
                    onItemSaved = {
                        // Após salvar, avisamos o widget para se atualizar
                        updateWidget()
                        // E fechamos a activity pop-up
                        finish()
                    }
                )
            }
        }
    }

    private fun updateWidget() {
        // 1. A ação agora é um nome customizado que nós criamos.
        val intent = Intent(this, FeiraToolWidgetProvider::class.java).apply {
            action = FeiraToolWidgetProvider.ACTION_DATA_UPDATED
        }

        // 2. Buscamos os IDs de todos os widgets do nosso app que estão na tela.
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, FeiraToolWidgetProvider::class.java)
        )

        // Se não houver widgets, não faz nada.
        if (ids.isEmpty()) return

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

        // 3. Enviamos o broadcast para que o onReceive do nosso Provider seja ativado.
        sendBroadcast(intent)
    }
}