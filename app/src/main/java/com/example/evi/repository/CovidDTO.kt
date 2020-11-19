package com.example.evi.repository

data class CovidDTO(
    val objectIdFieldName: String?,
    val fields: List<Field>?,
    val features: List<Features>?
)

data class Field(
    val name: String,
    val alias: String
)

data class Features(
    val attributes: Attribute?
)

data class Attribute(
    val FID: Int,
    val Kode_Provi: Int,
    val Provinsi: String,
    val Kasus_Posi: Int,
    val Kasus_Semb: Int,
    val Kasus_Meni: Int
) {
    val kasusPositif
        get() = Kasus_Posi.toString()

    val kasusSembuh
        get() = Kasus_Semb.toString()

    val kasusMeninggal
        get() = Kasus_Meni.toString()
}
