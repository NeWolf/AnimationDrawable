package com.newolf.widget.demo

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.newolf.widget.demo.ui.theme.AnimationDrawableTheme
import com.newolf.widget.drawable.apng.APNGDrawable
import com.newolf.widget.glide.registryAPNG


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationDrawableTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CustomView()
                }
            }
        }
    }
}


@Composable
fun CustomView() {
    var selectedItem by remember { mutableStateOf(0) }

    // Adds view to Compose
    AndroidView(
//        modifier = Modifier.wrapContentWidth(Alignment.Start), // Occupy the max size in the Compose UI tree
        factory = { context ->
            // Creates view


//
//            ImageView(context).apply {
//                // Sets up listeners for View -> Compose communication
//                val apngDrawable = APNGDrawable.fromAsset(context, "bj.png")
//                maxWidth = 547
//                maxHeight = 200
//                setImageDrawable(apngDrawable)
//
////                setImageResource(R.drawable.apng)
//
//            }
            Glide.get(context).registryAPNG()
            ImageView(context).apply {
                // Sets up listeners for View -> Compose communication
                val apngDrawable = APNGDrawable.fromResource(context, R.raw.girl)
//                scaleType=ImageView.ScaleType.CENTER_CROP
//                setImageDrawable(apngDrawable)
//                setImageResource(R.drawable.apng)
                Glide.with(this)
//                    .load("https://img2.baidu.com/it/u=1802630963,3044522616&fm=253&fmt=auto&app=138&f=PNG")
                    .load(R.raw.horse)
                    .into(this)

            }


        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AnimationDrawableTheme {
        CustomView()
    }
}