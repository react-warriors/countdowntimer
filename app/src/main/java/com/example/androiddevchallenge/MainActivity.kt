/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.challenge.adoptdoggy.ui.theme.background
import com.example.androiddevchallenge.ui.theme.CountDownTimerTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountDownTimerTheme {
                Surface(color = background) {
                    Timer()
                }
            }
        }
    }

    @Composable
    fun Timer() {
        var timerVal by remember { mutableStateOf(60) }
        var startTimer by remember { mutableStateOf(false) }
        val textAnimatedColor = remember { Animatable(Color.White) }
        LaunchedEffect(timerVal != 0) {
            textAnimatedColor.animateTo(
                Color.Yellow,
                animationSpec = infiniteRepeatable(TweenSpec(2000), RepeatMode.Reverse)
            )
        }
        if (startTimer) {
            LaunchedEffect(true) {
                while (timerVal != 0 && isActive) {
                    delay(1000)
                    timerVal -= 1
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                "Countdown timer", style = TextStyle(
                    color = White, fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Box(contentAlignment = Alignment.Center) {

                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircleShape(timerValue = timerVal)
                }

                Text(
                    text = "$timerVal",
                    style = TextStyle(fontSize = 120.sp, color = textAnimatedColor.value)
                )
            }

            Button(onClick = { startTimer = startTimer.not() }) {
                Icon(
                    if (startTimer == true) Icons.Default.Refresh
                    else Icons.Default.PlayArrow, "Play",
                    tint = Color.White
                )
            }
        }
    }

    @Composable
    fun CircleShape(timerValue: Int) {
        val animatedProgress = remember {
            Animatable(0f)
        }

        val animatedColor = remember {
            Animatable(Color.White)
        }

        LaunchedEffect(timerValue != 0) {
            animatedProgress.animateTo(
                1f,
                animationSpec = infiniteRepeatable(TweenSpec(2000), RepeatMode.Reverse)
            )
        }

        LaunchedEffect(key1 = timerValue != 0) {
            animatedColor.animateTo(
                Color.Gray,
                animationSpec = infiniteRepeatable(TweenSpec(2000), RepeatMode.Reverse)
            )
        }

        Box(
            Modifier
                .fillMaxSize(animatedProgress.value)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(animatedColor.value)
        )
    }

    @Composable
    fun Gesture() {
        val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    coroutineScope {
                        while (true) {
                            val position = awaitPointerEventScope {
                                awaitFirstDown().position
                            }
                            launch { offset.animateTo(position) }
                        }
                    }
                }
        ) {
            Circle(modifier = Modifier.offset { offset.value.toIntOffset() })
        }
    }

    @Composable
    fun Circle(modifier: Modifier) {
        Box(
            modifier = modifier.fillMaxSize()
                .apply { clip(androidx.compose.foundation.shape.CircleShape) })
    }

    private fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())
}

