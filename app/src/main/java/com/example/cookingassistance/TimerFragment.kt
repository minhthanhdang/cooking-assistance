package com.example.cookingassistance

import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast



class TimerFragment : Fragment() {
    private var second: Int = 0
    private var minute: Int = 0
    private var hour: Int = 0
    private lateinit var secondDisplay: EditText
    private lateinit var minuteDisplay: EditText
    private lateinit var hourDisplay: EditText
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var startPauseButton: Button
    private lateinit var resetButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secondDisplay = view.findViewById(R.id.second)
        minuteDisplay = view.findViewById(R.id.minute)
        hourDisplay = view.findViewById(R.id.hour)
        startPauseButton = view.findViewById(R.id.start_pause)
        resetButton = view.findViewById(R.id.reset)


        startPauseButton.setOnClickListener {
            when(startPauseButton.text.toString()) {
                "Start" -> startTimer()
                "Pause" -> pauseTimer()
            }
        }

        resetButton.setOnClickListener {
            countDownTimer.cancel()
            hour = 0
            minute = 0
            second = 0
            secondDisplay.setText(second.toString().padStart(2, '0'))
            minuteDisplay.setText(minute.toString().padStart(2, '0'))
            hourDisplay.setText(hour.toString().padStart(2, '0'))
            secondDisplay.isEnabled = true
            minuteDisplay.isEnabled = true
            hourDisplay.isEnabled = true
            startPauseButton.text = "Start"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    private fun startTimer() {
        second = secondDisplay.text.toString().toInt()
        minute = minuteDisplay.text.toString().toInt()
        hour = hourDisplay.text.toString().toInt()

        val totalTime: Long = ((hour * 3600 + minute * 60 + second + 1) * 1000).toLong()
        secondDisplay.isEnabled = false
        minuteDisplay.isEnabled = false
        hourDisplay.isEnabled = false
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = (millisUntilFinished/1000).toInt()
                val m = s/60
                hour = m/60

                second = s % 60
                minute = m % 60
                secondDisplay.setText(second.toString().padStart(2, '0'))
                minuteDisplay.setText(minute.toString().padStart(2, '0'))
                hourDisplay.setText(hour.toString().padStart(2, '0'))
            }

            override fun onFinish() {
                // Show the Times up! Toast
                Toast.makeText(requireContext(), "Times up!", Toast.LENGTH_LONG).show()

                // Ring the phone
                val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                val ringtone = RingtoneManager.getRingtone(requireContext() , ringtoneUri)
                ringtone.play()

                secondDisplay.isEnabled = true
                minuteDisplay.isEnabled = true
                hourDisplay.isEnabled = true

            }

        }
        countDownTimer.start()
        startPauseButton.text = "Pause"
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        secondDisplay.isEnabled = true
        minuteDisplay.isEnabled = true
        hourDisplay.isEnabled = true
        startPauseButton.text = "Start"

    }

}