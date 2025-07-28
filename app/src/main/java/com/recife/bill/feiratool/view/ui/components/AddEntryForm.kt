package com.recife.bill.feiratool.view.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.components.CustomInputText
import com.ifpe.edu.br.common.components.RectButton
import com.recife.bill.feiratool.model.repository.persistence.model.ShopItem
import com.recife.bill.feiratool.model.repository.persistence.model.ShoppItemEntry
import com.recife.bill.feiratool.viewmodel.AirPowerViewModel
import java.util.UUID

@Composable
fun AddEntryForm(
    mainViewModel: AirPowerViewModel,
    listId: String,
    navController: NavHostController,
    onItemSaved: () -> Unit = { navController.popBackStack() }
) {
    var newItemName by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var isNameError by rememberSaveable { mutableStateOf(false) }

    Column {
        Spacer(modifier = Modifier.height(15.dp))
        CustomInputText(
            inputFieldColors = defaultInputTextColors(),
            value = newItemName,
            onValueChange = {
                newItemName = it
                isNameError = it.isBlank()
            },
            label = "Nome do Produto",
            placeholder = "Ex: Café Santa Clara 500g",
            isError = isNameError,
            supportingText = { if (isNameError) Text("O nome é obrigatório") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Row(Modifier.fillMaxWidth()) {
            CustomInputText(
                inputFieldColors = defaultInputTextColors(),
                modifier = Modifier
                    .weight(1f),
                value = quantity,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        quantity = newValue
                    }
                },
                label = "Qtd.",
                textAlign = TextAlign.Center,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            // --- CAMPO PREÇO UNITÁRIO ---
            CustomInputText(
                inputFieldColors = defaultInputTextColors(),
                modifier = Modifier
                    .weight(2f),
                value = price,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*([.,])?\\d*\$"))) {
                        price = newValue
                    }
                },
                label = "Preço Unitário",
                placeholder = "0,00",
                textAlign = TextAlign.End,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        RectButton(
            colors = defaultButtonColors(),
            text = "Adicionar à Lista",
            enabled = newItemName.isNotBlank() && quantity.isNotBlank() && price.isNotBlank(),
            onClick = {
                val priceDouble = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                val quantityInt = quantity.toIntOrNull() ?: 0

                val newItem = ShopItem(
                    id = UUID.randomUUID().toString(),
                    name = newItemName
                )

                val newShoppItemEntry = ShoppItemEntry(
                    listId = listId,
                    itemId = newItem.id,
                    itemCount = quantityInt,
                    itemsValue = priceDouble * quantityInt,
                    priceAtTimeOfEntry = priceDouble
                )

                mainViewModel.addNewItemAndEntry(
                    shoppItem = newItem,
                    shoppItemEntry = newShoppItemEntry
                )

                newItemName = ""
                quantity = ""
                price = ""
                onItemSaved()
            }
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}