package com.gc.hi

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.baidu.aip.bodyanalysis.AipBodyAnalysis
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    lateinit var ivResult: ImageView
    lateinit var ivRandom: ImageView
    lateinit var pbLoading: ProgressBar
    private lateinit var res: Array<String>
    var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivResult = findViewById(R.id.iv_result)
        ivRandom = findViewById(R.id.iv_random)
        pbLoading = findViewById(R.id.pb_loading)

        res = arrayOf("1.jpg", "2.jpg", "3.jpg", "4.jpg")

        ivRandom.setImageBitmap(BitmapFactory.decodeStream(assets.open(res[index])))

        var aip = AipBodyAnalysis(BuildConfig.APP_ID, BuildConfig.API_KEY, BuildConfig.SECRET_KEY)

        ivRandom.setOnClickListener {
            index += 1
            if (index == 4) {
                index = 0
            }
            ivRandom.setImageBitmap(BitmapFactory.decodeStream(assets.open(res[index])))
            ivResult.setImageBitmap(null)
        }

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            pbLoading.visibility = View.VISIBLE
            Observable.just("cover")
                .map { handlerSegment(aip) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    pbLoading.visibility = View.GONE
                    ivResult.setImageBitmap(it)
                }
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            index = 0
            ivRandom.setImageBitmap(BitmapFactory.decodeStream(assets.open(res[index])))
            ivResult.setImageBitmap(null)
        }
    }

    private fun handlerSegment(client: AipBodyAnalysis): Bitmap? {
        val options = HashMap<String, String>()
        options["type"] = "foreground"

        val file: ByteArray = readFileByBytes(this, res[index])
        var res = client.bodySeg(file, options)
        var foreground = res.getString("foreground")
        return base64ToBitmap(foreground)
    }

    /**
     * 读取assert图片文件
     */
    private fun readFileByBytes(
        context: Context,
        filePath: String?
    ): ByteArray {
        val assetManager = context.assets
        val bos = ByteArrayOutputStream()
        var inputStream: InputStream? = null
        try {
            inputStream = assetManager.open(filePath!!)
            val bufSize = 1024
            val buffer = ByteArray(bufSize)
            var len: Int
            while (-1 != inputStream.read(buffer, 0, bufSize).also { len = it }) {
                bos.write(buffer, 0, len)
            }
            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            try {
                bos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ByteArray(0)
    }

    /**
     * base64转bitmap
     */
    private fun base64ToBitmap(base64Data: String?): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
