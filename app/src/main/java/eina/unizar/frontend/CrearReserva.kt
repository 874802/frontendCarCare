package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime

data class NuevaReservaData(
    val vehiculoId: String,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val tipo: TipoReserva,
    val notas: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaReservaScreen(
    vehiculos: List<Vehiculo>,
    onBackClick: () -> Unit,
    onCrearReserva: (NuevaReservaData) -> Unit
) {
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(vehiculos.firstOrNull()) }
    var fecha by remember { mutableStateOf(LocalDate.now()) }
    var horaInicio by remember { mutableStateOf("09:00") }
    var horaFin by remember { mutableStateOf("14:00") }
    var tipoSeleccionado by remember { mutableStateOf(TipoReserva.TRABAJO) }
    var notas by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var expandedVehiculo by remember { mutableStateOf(false) }
    var disponible by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEF4444)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Nueva Reserva",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Detalles de la reserva",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de vehículo
            Text(
                text = "Vehículo",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedVehiculo,
                onExpandedChange = { expandedVehiculo = it }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .menuAnchor()
                        .clickable { expandedVehiculo = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        vehiculoSeleccionado?.let { vehiculo ->
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        vehiculo.tipo.color.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vehiculo.tipo.icon,
                                    contentDescription = vehiculo.tipo.name,
                                    tint = vehiculo.tipo.color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${vehiculo.nombre} - ${vehiculo.matricula}",
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = expandedVehiculo,
                    onDismissRequest = { expandedVehiculo = false }
                ) {
                    vehiculos.forEach { vehiculo ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = vehiculo.tipo.icon,
                                        contentDescription = null,
                                        tint = vehiculo.tipo.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("${vehiculo.nombre} - ${vehiculo.matricula}")
                                }
                            },
                            onClick = {
                                vehiculoSeleccionado = vehiculo
                                expandedVehiculo = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Fecha
            Text(
                text = "Fecha",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = fecha.toString(),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDatePicker = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFEF4444),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Fecha")
                },
                enabled = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hora inicio y fin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hora de inicio",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = { horaInicio = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Hora")
                        }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hora de fin",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = horaFin,
                        onValueChange = { horaFin = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = "Hora")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tipo de reserva
            Text(
                text = "Tipo de reserva",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TipoReservaCard(
                    tipo = TipoReserva.TRABAJO,
                    selected = tipoSeleccionado == TipoReserva.TRABAJO,
                    onClick = { tipoSeleccionado = TipoReserva.TRABAJO },
                    modifier = Modifier.weight(1f)
                )
                TipoReservaCard(
                    tipo = TipoReserva.PERSONAL,
                    selected = tipoSeleccionado == TipoReserva.PERSONAL,
                    onClick = { tipoSeleccionado = TipoReserva.PERSONAL },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Notas
            Text(
                text = "Notas (opcional)",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                placeholder = { Text("Añade detalles sobre tu reserva...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFEF4444),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Verificación de disponibilidad
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (disponible) Color(0xFFECFDF5) else Color(0xFFFEE2E2)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (disponible) Color(0xFF10B981) else Color(0xFFEF4444)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (disponible) Color(0xFF10B981) else Color(0xFFEF4444),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (disponible) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (disponible) "Horario disponible" else "Conflicto detectado",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (disponible) Color(0xFF065F46) else Color(0xFF991B1B)
                        )
                        Text(
                            text = if (disponible)
                                "No hay conflictos con otras reservas"
                            else
                                "Ya existe una reserva en este horario",
                            fontSize = 12.sp,
                            color = if (disponible) Color(0xFF047857) else Color(0xFFDC2626)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Crear reserva
            Button(
                onClick = {
                    vehiculoSeleccionado?.let { vehiculo ->
                        onCrearReserva(
                            NuevaReservaData(
                                vehiculoId = vehiculo.id,
                                fecha = fecha,
                                horaInicio = LocalTime.parse(horaInicio),
                                horaFin = LocalTime.parse(horaFin),
                                tipo = tipoSeleccionado,
                                notas = notas
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                enabled = disponible && vehiculoSeleccionado != null
            ) {
                Text(
                    text = "Crear Reserva",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun TipoReservaCard(
    tipo: TipoReserva,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) tipo.color else Color.White
    val textColor = if (selected) Color.White else Color(0xFF1F2937)
    val borderWidth = if (selected) 0.dp else 1.dp

    Card(
        modifier = modifier
            .height(55.dp)
            .border(borderWidth, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        if (selected) Color.White.copy(alpha = 0.2f) else tipo.color.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tipo == TipoReserva.TRABAJO)
                        Icons.Default.Build
                    else
                        Icons.Default.Person,
                    contentDescription = null,
                    tint = if (selected) Color.White else tipo.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = tipo.nombre,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}