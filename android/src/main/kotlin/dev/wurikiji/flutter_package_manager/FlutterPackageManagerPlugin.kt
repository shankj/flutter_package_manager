package dev.wurikiji.flutter_package_manager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import io.flutter.Log
import io.flutter.plugin.common.JSONMethodCodec
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.json.JSONArray
import java.io.ByteArrayOutputStream

const val METHOD_CHANNEL = "dev.wurikiji.flutter_package_manager.method_channel"
const val TAG = "Flutter Package Manager"
class FlutterPackageManagerPlugin: MethodCallHandler {
  companion object {

    var sContext: Context? = null
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(),
              METHOD_CHANNEL,
              JSONMethodCodec.INSTANCE)
      channel.setMethodCallHandler(FlutterPackageManagerPlugin())
      sContext = registrar.context().applicationContext
      Log.i(TAG, "Register with ${registrar.context().packageName}")
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when(call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }
      "getPackageInfo" -> {
          val args = call.arguments as JSONArray
          result.success(getPackageInfo(args[0] as String))
      }
      else -> {
        result.notImplemented()
      }
    }
    if (call.method == "getPlatformVersion") {
    } else {
    }
  }
  private fun getPackageInfo(packageName: String) : java.util.HashMap<String, Any?> {
    val info = java.util.HashMap<String, Any?>()
    val appInfo: ApplicationInfo = sContext!!.packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val appName: String? = sContext!!.packageManager.getApplicationLabel(appInfo).toString()
    val appIcon: Drawable = sContext!!.packageManager.getApplicationIcon(appInfo.packageName) ?: sContext!!.getDrawable(R.drawable.ic_launcher)
    val byteImage = drawableToBase64String(appIcon)

    info["packageName"] = appInfo.packageName
    info["appName"] = appName
    info["appIcon"] = byteImage
    return info
  }

  private fun drawableToBitmap(drawable: Drawable) : Bitmap {
    var bitmap: Bitmap? = null

    if (drawable is BitmapDrawable) {
      if (drawable.bitmap != null) {
        return drawable.bitmap
      }
    }

    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
      bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
      bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    drawable.draw(canvas)
    return bitmap
  }

  private fun drawableToBase64String(drawable: Drawable) : String{
    val bitmap: Bitmap = drawableToBitmap(drawable)
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
  }


}
