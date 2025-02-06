package com.example.q1
import android.os.Bundle
import androidx.compose.material3.Divider
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlovRecipeCard()
        }
    }
}


@Composable
fun PlovRecipeCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Azerbaijani Plov",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )

            Divider(color = Color.Red, thickness = 2.dp, modifier = Modifier.padding(9.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plov),
                    contentDescription = "Azerbaijani Plov",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
            Text(
                text = "- 2 cups Basmati rice\n" +
                        "- 3 tbsp Butter\n" +
                        "- 1/2 tsp Saffron\n" +
                        "- 3 cups Water\n" +
                        "- Salt to taste",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.Black
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp),
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
            Text(
                text = "1. Rinse rice under cold water until clear.\n" +
                        "2. Soak saffron in 2 tbsp warm water for color.\n" +
                        "3. Boil water and add rice with salt.\n" +
                        "4. Add saffron water and butter, then simmer for 15 minutes.\n" +
                        "5. Fluff the rice and serve warm.",
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.Black
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewPlovRecipeCard(){
    PlovRecipeCard()
}