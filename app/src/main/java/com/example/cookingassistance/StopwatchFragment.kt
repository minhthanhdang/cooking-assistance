package com.example.cookingassistance

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer


class StopwatchFragment : Fragment() {

    private lateinit var chronometer: Chronometer
    private lateinit var startPauseButton: Button
    private lateinit var resetButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind the buttons and chronometer
        startPauseButton = view.findViewById(R.id.start_pause)
        resetButton = view.findViewById(R.id.reset)
        chronometer = view.findViewById(R.id.chronometer)
        chronometer.base = SystemClock.elapsedRealtime()


        // START BUTTON
        startPauseButton.setOnClickListener {
            when(startPauseButton.text.toString()) {
                "Start" -> startChronometer()
                "Pause" -> pauseChronometer()
            }
        }


        // RESET BUTTON
        resetButton.setOnClickListener {
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            startPauseButton.text = getString(R.string.start)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stopwatch, container, false)
    }


    // START THE CHRONOMETER
    private fun startChronometer() {
        chronometer.start()
        startPauseButton.text = getString(R.string.pause)
    }


    // PAUSE THE CHRONOMETER
    private fun pauseChronometer() {
        chronometer.stop()
        startPauseButton.text = getString(R.string.start)
    }
}