package com.gc.hi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.baidu.aip.bodyanalysis.AipBodyAnalysis
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    lateinit var ivResult: ImageView
    lateinit var ivRandom: ImageView
    lateinit var pbLoading: ProgressBar
    var res = arrayOf("1.jpg", "2.jpg", "3.jpg", "4.jpg")
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ivResult = findViewById(R.id.iv_result)
        ivRandom = findViewById(R.id.iv_random)
        pbLoading = findViewById(R.id.pb_loading)
        ivRandom.setImageBitmap(BitmapFactory.decodeStream(assets.open(res[index])))

        var aip = AipBodyAnalysis(
            "", "", ""
        )

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

        val file: ByteArray = AssertUtils.readFileByBytes(this, res[index])
        var res = client.bodySeg(file, options)
        var foreground = res.getString("foreground")
        return AssertUtils.base64ToBitmap(foreground)
    }
}
