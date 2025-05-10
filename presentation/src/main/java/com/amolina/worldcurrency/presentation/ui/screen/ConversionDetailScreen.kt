package com.amolina.worldcurrency.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amolina.worldcurrency.presentation.viewmodel.ConversionDetailViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionDetailScreen(
    navController: NavController,
    conversionId: Long,
    viewModel: ConversionDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(conversionId) {
        viewModel.load(conversionId)
    }

    val conversion by viewModel.conversion.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conversion Detail") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        if (conversion == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val c = conversion!!
            val date = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                .format(Date(c.timestamp))

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val formattedAmount = DecimalFormat("#,##0.##").format(c.amount)
                val formattedResult = DecimalFormat("#,##0.##").format(c.result)
                item { Text("From: ${c.fromCode} - ${c.fromName}") }
                item { Text("To: ${c.toCode} - ${c.toName}") }
                item { Text("Amount: $formattedAmount") }
                item { Text("Rate: ${c.rate}") }
                item { Text("Result: $formattedResult") }
                item {
                    Text(
                        "Date: $date",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

