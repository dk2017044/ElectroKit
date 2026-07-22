package com.example.electrokit.domain.model

data class ComponentPin(
    val number: String,
    val name: String,
    val description: String
)

data class ComponentDetail(
    val id: String,
    val name: String,
    val category: String, // e.g. Transistor, MOSFET, Voltage Regulator, Diode, Op-Amp, Timer IC, Sensor
    val type: String,     // e.g. NPN General Purpose Transistor, TO-92
    val packageType: String, // TO-92, TO-220, DIP-8, DIP-14, DO-41, etc.
    val symbolIcon: String,  // Icon / Drawing reference name
    val pins: List<ComponentPin>,
    val workingPrinciple: String,
    val specifications: Map<String, String>, // Key specs (e.g. V_max: "45V", I_max: "100mA")
    val applications: List<String>,
    val equivalents: List<String>,
    val isFavorite: Boolean = false
)
