package com.example.demo

/**
 * Created by sivannikov on 24.09.2023 21:26
 */
data class Invoice(val id: String? = null) {

    override fun toString(): String {
        return "Invoice [id=$id]"
    }
}
