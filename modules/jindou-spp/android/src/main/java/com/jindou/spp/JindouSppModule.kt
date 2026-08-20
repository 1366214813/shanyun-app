package com.jindou.spp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class JindouSppModule : Module() {
  private var socket: BluetoothSocket? = null
  private var outputStream: OutputStream? = null
  private var inputStream: InputStream? = null

  private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

  override fun definition() = ModuleDefinition {
    Name("JindouSpp")

    AsyncFunction("nativeSupport") { true }

    AsyncFunction("listBondedDevices") { promise: Promise ->
      Log.i("JindouSpp", "listBondedDevices called")
      try {
        val adapter = BluetoothAdapter.getDefaultAdapter()
          ?: throw Exceptions.IllegalStateException("No bluetooth adapter")
        Log.i("JindouSpp", "adapter=$adapter, isEnabled=${adapter.isEnabled}")
        val devices = adapter.bondedDevices
        Log.i("JindouSpp", "bondedDevices size=${devices.size}")
        val result = devices.map { device ->
          mapOf(
            "id" to device.address,
            "name" to (device.name ?: ""),
            "bonded" to (device.bondState == BluetoothDevice.BOND_BONDED),
          )
        }
        Log.i("JindouSpp", "result=$result")
        promise.resolve(result)
      } catch (e: Throwable) {
        Log.e("JindouSpp", "listBondedDevices error", e)
        promise.reject("SPP_LIST_BONDED_FAILED", e.message ?: "List bonded failed", e)
      }
    }

    AsyncFunction("connect") { address: String, promise: Promise ->
      try {
        disconnectInternal()
        val adapter = BluetoothAdapter.getDefaultAdapter()
          ?: throw Exceptions.IllegalStateException("No bluetooth adapter")
        val device = adapter.getRemoteDevice(address)
        val sock = device.createInsecureRfcommSocketToServiceRecord(sppUuid)
        sock.connect()
        socket = sock
        outputStream = sock.outputStream
        inputStream = sock.inputStream
        promise.resolve(mapOf("connected" to true, "name" to (device.name ?: address)))
      } catch (e: Exception) {
        try { socket?.close() } catch (_: Exception) {}
        socket = null
        outputStream = null
        inputStream = null
        promise.reject("SPP_CONNECT_FAILED", e.message ?: "Connect failed", e)
      }
    }

    AsyncFunction("disconnect") {
      disconnectInternal()
    }

    AsyncFunction("isConnected") { socket?.isConnected == true }

    AsyncFunction("write") { base64: String, promise: Promise ->
      try {
        val out = outputStream ?: throw Exceptions.IllegalStateException("Not connected")
        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
        out.write(bytes)
        out.flush()
        promise.resolve(mapOf("written" to bytes.size))
      } catch (e: Exception) {
        promise.reject("SPP_WRITE_FAILED", e.message ?: "Write failed", e)
      }
    }

    AsyncFunction("readAvailable") { count: Int, promise: Promise ->
      try {
        val inp = inputStream ?: throw Exceptions.IllegalStateException("Not connected")
        val avail = inp.available()
        val max = if (avail <= 0) 0 else minOf(avail, count.coerceAtLeast(1))
        val buf = ByteArray(max)
        var total = 0
        while (total < max) {
          val r = inp.read(buf, total, max - total)
          if (r < 0) break
          total += r
        }
        val b64 = android.util.Base64.encodeToString(buf.copyOf(total), android.util.Base64.NO_WRAP)
        promise.resolve(b64)
      } catch (e: Exception) {
        promise.reject("SPP_READ_FAILED", e.message ?: "Read failed", e)
      }
    }

    AsyncFunction("getSelectedAdapterAddress") {
      null
    }
  }

  private fun disconnectInternal() {
    try { socket?.close() } catch (_: IOException) {}
    socket = null
    outputStream = null
    inputStream = null
  }
}