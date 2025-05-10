package com.amolina.worldcurrency.presentation.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.amolina.worldcurrency.domain.model.Currency

@Composable
fun CurrencySelector(
    label: String,
    currencies: List<Currency>,
    selected: Currency?,
    exclude: String? = null,
    onSelect: (Currency) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDialog = true }
        ) {
            Text(selected?.code ?: "Select")
        }

        if (showDialog) {
            CurrencySelectorDialog(
                currencies = currencies.filterNot { it.code == exclude },
                onDismiss = { showDialog = false },
                onCurrencySelected = {
                    showDialog = false
                    onSelect(it)
                }
            )
        }
    }
}
