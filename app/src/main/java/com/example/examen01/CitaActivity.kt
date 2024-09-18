package com.example.examen01

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CitaActivity : AppCompatActivity() {
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var btnAgendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cita)

        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        btnAgendar = findViewById(R.id.btn_agendar)

        // Configurar el TimePicker para usar el formato de 24 horas
        timePicker.setIs24HourView(true)

        btnAgendar.setOnClickListener {
            val date = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
            val time = "${timePicker.hour}:${timePicker.minute}"
            Toast.makeText(this, "Cita agendada para $date a las $time", Toast.LENGTH_SHORT).show()
        }
    }
}
