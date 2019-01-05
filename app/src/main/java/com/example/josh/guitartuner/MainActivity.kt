package com.example.josh.guitartuner

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.MediaRecorder
import android.media.AudioRecord
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.lang.Math
import android.os.Looper
import android.provider.Settings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val LOG_TAG = "Test"
val RECORDING_RATE = 44100
val CHANNEL = AudioFormat.CHANNEL_IN_MONO
val FORMAT = AudioFormat.ENCODING_PCM_16BIT
val minBuffSize = AudioRecord.getMinBufferSize(RECORDING_RATE,CHANNEL,FORMAT)

val recorder = AudioRecord(MediaRecorder.AudioSource.MIC ,RECORDING_RATE ,CHANNEL ,FORMAT ,minBuffSize)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Log.v(LOG_TAG, "Test")

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) //Checking for permission. Requests if not there
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
            val requestCode: Int = 1
            Log.v(LOG_TAG, "Permission not granted")
            ActivityCompat.requestPermissions(this, permissions, requestCode)
        } else {
            Log.v(LOG_TAG, "Permission is granted")
        }

    }

    fun startRecord(view: View) {


            var isRecording: Boolean = true
            var audio_data: ShortArray = ShortArray(minBuffSize)
            val output: DataOutputStream? = null
            var sum: Double = 0.0
            var readSize: Int = recorder.read(audio_data, 0, minBuffSize)
            var amplitude: Double

            recorder.startRecording()
            Log.v(LOG_TAG, "Audio is Recording")

            GlobalScope.launch {

                while (isRecording == true) { // Async task probably
                    //sum = 0.0

                    for (i in 1 until readSize) {
                        //output?.writeShort(audio_data[i].toInt())
                        sum += audio_data[i] * audio_data[i]

                    }

                    if (readSize > 0) {

                        amplitude = sum / readSize
                        Log.v(LOG_TAG,"ReadSize ${amplitude.toInt()}")

                        progressBar.setProgress(Math.sqrt(amplitude).toInt())
                    }
                        amplitude = 0.0
                        sum = 0.0


                }
            }
        }// End of Func


        fun stopFunc(v: View) { // Dont need to setOnClickListener because functionality of button is defined in xml

            //Should check if recording first
            recorder.stop()
            recorder.release()
            Log.v(LOG_TAG, "Stop button")


        }
    }//End of Main



