package com.recife.bill.feiratool.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.recife.bill.feiratool.R
import com.recife.bill.feiratool.model.repository.Repository
import com.recife.bill.feiratool.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeiraToolWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_DATA_UPDATED = "com.recife.bill.feiratool.ACTION_DATA_UPDATED"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = Repository.getInstance()
            val latestList = repository.getLatestList()
            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.feiratool_widget_layout)

                // Popula a UI do widget com os dados da última lista
                if (latestList != null) {
                    views.setTextViewText(R.id.widget_list_name, latestList.shoppList.name)
                    val totalValueText = "Total: R$ ${"%.2f".format(latestList.shoppList.listValue)}"
                    views.setTextViewText(R.id.widget_total_value, totalValueText)
                } else {
                    views.setTextViewText(R.id.widget_list_name, "Nenhuma lista encontrada")
                    views.setTextViewText(R.id.widget_total_value, "Total: R$ 0,00")
                }

                // --- Configurando a Interação ---

                // 1. Abrir a tela de "Nova Entrada" ao clicar no botão '+'
                // Precisamos passar o ID da última lista para a tela saber onde adicionar o item.
                val listId = latestList?.shoppList?.id
                if (listId != null) {
                    // Crie um Intent que aponta para a MainActivity (ou uma activity intermediária)
                    // que possa navegar para a tela de nova entrada.
                    // A forma mais fácil é a MainActivity lidar com um "deep link" ou extra.
                    val addIntent = Intent(context, WidgetAddItemActivity::class.java).apply {
                        // Passamos o ID da lista para a Activity saber onde adicionar o item
                        putExtra("LIST_ID_EXTRA", listId)
                        // Flags para garantir que a activity seja nova
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val addPendingIntent = PendingIntent.getActivity(
                        context, 1, addIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_add_button, addPendingIntent)
                }

                // 2. Abrir o app ao clicar no resto do widget
                val openAppIntent = Intent(context, MainActivity::class.java)
                val openAppPendingIntent = PendingIntent.getActivity(
                    context, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE
                )
                // Vamos usar o nome da lista como o alvo do clique para abrir o app
                views.setOnClickPendingIntent(R.id.widget_list_name, openAppPendingIntent)

                // Instrui o AppWidgetManager a atualizar o widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    /**
     * O onReceive é chamado para TODOS os broadcasts.
     * Nós o usamos para capturar nosso aviso customizado.
     */
    override fun onReceive(context: Context, intent: Intent) {
        // Chama o onReceive da superclasse para garantir que o onUpdate continue funcionando
        super.onReceive(context, intent)

        // Se a ação for a nossa, força a atualização dos widgets
        if (intent.action == ACTION_DATA_UPDATED) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, FeiraToolWidgetProvider::class.java)
            )
            updateAllWidgets(context, appWidgetManager, appWidgetIds)
        }
    }

    /**
     * Função que contém a lógica real de atualização, agora reutilizável.
     */
    private fun updateAllWidgets(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = Repository.getInstance()
            val latestList = repository.getLatestList()

            for (appWidgetId in appWidgetIds) {
                // ... (toda a sua lógica de RemoteViews e PendingIntent continua aqui, exatamente como antes)
                val views = RemoteViews(context.packageName, R.layout.feiratool_widget_layout)
                // ...
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}