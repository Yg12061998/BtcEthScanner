package com.yogigupta1206.btcethscanner.interfaces

interface QRCodeFoundListener {
    fun onQRCodeFound(qrCode: String?)
    fun qrCodeNotFound()
}