package com.taximetro.receipt

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.taximetro.model.Receipt
import java.io.OutputStream
import java.util.UUID

class ReceiptPrinter {

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    companion object {
        private const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    }

    fun connect(macAddress: String): Boolean {
        return try {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter == null || !adapter.isEnabled) return false

            val device: BluetoothDevice = adapter.getRemoteDevice(macAddress)
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
            adapter.cancelDiscovery()
            socket?.connect()
            outputStream = socket?.outputStream
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun print(receipt: Receipt): Boolean {
        return try {
            val os = outputStream ?: return false
            val lines = receipt.formatLines()

            os.write(byteArrayOf(0x1B, 0x40.toByte())) // init printer
            os.write(byteArrayOf(0x1B, 0x61, 0x01)) // center align

            for (line in lines) {
                os.write(line.toByteArray(charset("UTF-8")))
                os.write("\n".toByteArray())
            }

            os.write("\n\n\n".toByteArray())
            os.write(byteArrayOf(0x1B, 0x69)) // cut paper
            os.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (_: Exception) {
        } finally {
            outputStream = null
            socket = null
        }
    }
}
