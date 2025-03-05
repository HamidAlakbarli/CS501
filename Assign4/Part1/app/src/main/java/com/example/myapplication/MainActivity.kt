package com.example.myapplication
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration

// Data class for Product
data class Product(val name: String, val price: String, val description: String)

// Sample products
val products = listOf(
    Product("Product A", "$100", "This is a great product A."),
    Product("Product B", "$150", "This is product B with more features."),
    Product("Product C", "$200", "Premium product C.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingApp()
        }
    }
}

@Composable
fun ShoppingApp() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            ProductList(onProductSelected = { selectedProduct = it }, modifier = Modifier.weight(1f))
            ProductDetails(product = selectedProduct, modifier = Modifier.weight(1f))
        }
    } else {
        var showDetails by remember { mutableStateOf(false) }

        if (showDetails) {
            ProductDetails(product = selectedProduct, onBack = { showDetails = false })
        } else {
            ProductList(onProductSelected = { selectedProduct = it; showDetails = true })
        }
    }
}

@Composable
fun ProductList(onProductSelected: (Product) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
        items(products) { product ->
            Text(
                text = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable { onProductSelected(product) },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ProductDetails(product: Product?, onBack: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (onBack != null) {
            Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Back")
            }
        }
        if (product != null) {
            Text(text = product.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Price: ${product.price}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(text = "Select a product to view details.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShoppingApp() {
    ShoppingApp()
}