package com.example.kursain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import java.net.URL

import java.net.HttpURLConnection
import org.json.JSONObject

import android.util.Log
import android.widget.EditText
import java.net.URLEncoder

import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.io.*
import java.lang.StringBuilder
import javax.net.ssl.HttpsURLConnection

/**
 * @author Seroja Grigoryan
 * @author Martin Mirzoyan
 */

const val MY_PERMISSIONS_REQUEST_INTERNET: Int = 1
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permission()
        val login_plain = findViewById<EditText>(R.id.login)
        val pass_plain = findViewById<EditText>(R.id.pass)
        val result_view = findViewById<TextView>(R.id.result)
        val auth_button = findViewById<Button>(R.id.auth)
        val reg_button = findViewById<Button>(R.id.reg)
        // var id = 1
        auth_button.setOnClickListener {
            val auth = Authorization(
                login_plain.text.toString(),
                pass_plain.text.toString(),/*id,*/
                result_view

            )
            auth.execute(null)
        }
        reg_button.setOnClickListener {
            val reg = Registration(
                login_plain.text.toString(),
                pass_plain.text.toString(),/*id,*/
                result_view,
                this
            )
            reg.execute(null)
        }


    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_INTERNET -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_LONG).show()
                    // functionality that depends on this permission.
                }
                return
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            }
        }
    }
    private fun permission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Toast.makeText(this, "No Permission", Toast.LENGTH_LONG).show()
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.INTERNET
                )
            ) {
                // No explanation needed, we can request the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.INTERNET),
                        MY_PERMISSIONS_REQUEST_INTERNET
                    )
                }

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            // Toast.makeText(this, "No Permissions", Toast.LENGTH_LONG).show()
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        }
    }


    class Authorization(
        private val login_plain: String,
        private val pass_plain: String,/*var id:Int,*/
        private val result_view: TextView
    ) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {

            val url = URL("http://spicy-chipmunk-58.serverless.social/authenticate")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            //conn.doInput = true
            conn.connect()
            val jsonParam = JSONObject()
            jsonParam.put("username", login_plain)
            jsonParam.put("password", pass_plain)
            //jsonParam.put("id",id )
            //id+=1
            val jsonInputString = jsonParam.toString()
            val os = conn.outputStream
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
            val br = BufferedReader( InputStreamReader(conn.inputStream, "utf-8"))

            val response = StringBuilder()
            var responseLine=br.readLine()
            while ((responseLine) != null) {
                response.append(responseLine.trim())
                responseLine = br.readLine()
            }
            val resultJson = response.toString()
            Log.d("MyLog",resultJson)
            Log.i("STATUS", conn.responseCode.toString())
            Log.i("MSG", conn.responseMessage)

            os.flush()
            os.close()
            br.close()
            conn.disconnect()
            secondRequest(resultJson)
            return resultJson
        }

        private fun secondRequest(result: String) {

            val token = JSONObject(result).get("jwt")
            val url = URL("http://spicy-chipmunk-58.serverless.social/hello")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.doInput=true
            conn.addRequestProperty("Authorization", "Bearer $token")
            conn.connect()
            Log.d("mylog","connected")
            //val out = ByteArrayOutputStream()
            val br = BufferedReader( InputStreamReader(conn.inputStream, "utf-8"))

            val response = StringBuilder()
            var responseLine=br.readLine()
            while ((responseLine) != null) {
                response.append(responseLine.trim())
                responseLine = br.readLine()
            }
            val resultJson = response.toString()
            //out.close()
            Handler(Looper.getMainLooper()).post {
                result_view.text = resultJson
            }
            Log.d("mylog",resultJson)
            //result_view.text = JSONObject(resultJson).getString("data")
            br.close()
            conn.disconnect()

        }

    }


    class Registration(
        private val login_plain: String,
        private val pass_plain: String,/*var id:Int,*/
        private val result_view: TextView,
        val context: Context
    ) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL("http://spicy-chipmunk-58.serverless.social/registration")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            //conn.doInput = true
            //conn.connect()

            val jsonParam = JSONObject()
            jsonParam.put("username", login_plain)
            jsonParam.put("password", pass_plain)
            //jsonParam.put("id",id )
            //id+=1
            val jsonInputString = jsonParam.toString()
            val os = conn.outputStream
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
            val br = BufferedReader( InputStreamReader(conn.inputStream, "utf-8"))

            val response = StringBuilder()
            var responseLine=br.readLine()
            while ((responseLine) != null) {
                response.append(responseLine.trim())
                responseLine = br.readLine()
            }
            Log.d("MyLog",response.toString())
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context,"Registration completed.", Toast.LENGTH_LONG).show()
            }

            os.flush()
            os.close()
            br.close()

            //conn.disconnect()

            return "ok"
        }


    }
}

