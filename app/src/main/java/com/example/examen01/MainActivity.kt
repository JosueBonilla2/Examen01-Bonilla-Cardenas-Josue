package com.example.examen01

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var solicitud: Solicitud
    private lateinit var etCurp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etDomicilio: EditText
    private lateinit var etIngreso: EditText
    private lateinit var spinnerTipoPrestamo: Spinner
    private lateinit var btnValidar: Button
    private lateinit var btnLimpiar: Button
    private val CHANNEL_ID = "prestamo_channel"
    private val NOTIFICATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        etCurp = findViewById(R.id.etCurp)
        etNombre = findViewById(R.id.etNombre)
        etApellidos = findViewById(R.id.etApellidos)
        etDomicilio = findViewById(R.id.etDomicilio)
        etIngreso = findViewById(R.id.etCantidadIngreso)
        spinnerTipoPrestamo = findViewById(R.id.spTipoPrestamo)
        btnValidar = findViewById(R.id.btnValidar)
        btnLimpiar = findViewById(R.id.btnLimpiar)

        // Inicializar Spinner
        val tiposPrestamo = arrayOf("personal", "negocio", "vivienda")
        spinnerTipoPrestamo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tiposPrestamo)

        // Configurar botones
        btnValidar.setOnClickListener { validarSolicitud() }
        btnLimpiar.setOnClickListener { limpiarCampos() }

        // Crear canal de notificación
        createNotificationChannel()

        // Verificar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_CODE)
            }
        }
    }

    private fun validarSolicitud() {
        val curp = etCurp.text.toString()
        val nombre = etNombre.text.toString()
        val apellidos = etApellidos.text.toString()
        val domicilio = etDomicilio.text.toString()
        val ingresoStr = etIngreso.text.toString()
        val tipoPrestamo = spinnerTipoPrestamo.selectedItem.toString()

        val ingreso = ingresoStr.toDoubleOrNull()

        if (ingreso == null) {
            Toast.makeText(this, "Por favor ingresa una cantidad de ingreso válida", Toast.LENGTH_SHORT).show()
        } else {
            solicitud = Solicitud(curp, nombre, apellidos, domicilio, ingreso, tipoPrestamo)

            if (solicitud.validarIngreso()) {
                mostrarNotificacion()
            } else {
                Toast.makeText(this, "No apto para el préstamo de $tipoPrestamo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun limpiarCampos() {
        etCurp.text.clear()
        etNombre.text.clear()
        etApellidos.text.clear()
        etDomicilio.text.clear()
        etIngreso.text.clear()
        spinnerTipoPrestamo.setSelection(0)
    }

    private fun mostrarNotificacion() {
        val citaIntent = Intent(this, CitaActivity::class.java)
        val citaPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, citaIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val prestamosIntent = Intent(this, PrestamosActivity::class.java)
        val prestamosPendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, prestamosIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notificaciones)
            .setContentTitle("Préstamo Aprobado")
            .setContentText("Seleccione una opción para continuar")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_fecha, "Cita", citaPendingIntent)
            .addAction(R.drawable.ic_prestamo, "Préstamos", prestamosPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(1, builder.build())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Préstamo"
            val descriptionText = "Canal para notificaciones de préstamos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
