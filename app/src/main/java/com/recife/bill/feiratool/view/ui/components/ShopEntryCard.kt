package com.recife.bill.feiratool.view.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ifpe.edu.br.common.components.CustomCard
import com.recife.bill.feiratool.model.repository.persistence.model.ShoppEntryWithItem
import com.recife.bill.feiratool.view.ui.theme.tb_primary_light

@Composable
fun ShopEntryCard(
    entryWithItem: ShoppEntryWithItem,
    navController: NavHostController,
) {
    CustomCard(
        modifier = Modifier.padding(vertical = 4.dp),
        paddingStart = 16.dp,
        paddingEnd = 16.dp,
        paddingTop = 12.dp,
        paddingBottom = 12.dp,
        layouts = listOf {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(entryWithItem.shopItem.name)
                            }
                        },
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${entryWithItem.entry.itemCount} x R$${"%.2f".format(entryWithItem.entry.priceAtTimeOfEntry)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp) // Adiciona um respiro
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    Text(
                        text = buildAnnotatedString {
                            append("Sub total: ")
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = tb_primary_light
                                )
                            ) {
                                append("R$ ${"%.2f".format(entryWithItem.entry.itemsValue)}")
                            }
                        },
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}