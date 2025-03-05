package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingCartScreen()
        }
    }
}

@Composable
fun ShoppingCartScreen()
    //got help
    {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val cartItems = listOf(
        CartItem("Banana", 3.0, 1),
        CartItem("Apple", 5.0, 2),
        CartItem("Milk", 9.0, 1),
        CartItem("Water", 2.0, 1)
    )

    val totalCost = cartItems.sumOf { it.price * it.quantity }
    //researched
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Shopping Cart", fontSize = 24.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(16.dp)) {
                    cartItems.forEach { item ->
                        CartItemRow(item)
                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total:", fontSize = 20.sp, color = Color.Black)
                    Text(text = "$${totalCost}", fontSize = 20.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Ordered")
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text(text = "Checkout", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    )
}

data class CartItem(val name: String, val price: Double, val quantity: Int)

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = item.name, fontSize = 18.sp, color = Color.Black)
            Text(text = "Qty: ${item.quantity}", fontSize = 14.sp, color = Color.Gray)
        }
        Text(text = "$${item.price * item.quantity}", fontSize = 18.sp, color = Color.Black)
    }
}

@Preview
@Composable
fun PreviewShoppingCartScreen() {
    ShoppingCartScreen()
}
