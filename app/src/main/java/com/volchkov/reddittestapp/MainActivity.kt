package com.volchkov.reddittestapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStream
import java.net.URL
import kotlin.math.round

class MainActivity : AppCompatActivity(), Adapter.ItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    val web = "https://www.reddit.com/.json"
    val progressBar : ProgressBar = findViewById<View>(R.id.progressBar) as ProgressBar
    val auth = ArrayList<String>()
    val comm = ArrayList<String>()
    val date = ArrayList<String>()
    val thumbnail = ArrayList<Bitmap>()
    val content = ArrayList<String>()

    //Проверяем наличие интернета (если есть - пуск программы, нет - сообщение и выход)
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
    if (capabilities != null) {


        //Поиск авторов публикации в .json
        class MyThread : Thread() {
            override fun run() {

                var author : String? = null
                val json = URL(web).readText()
                var k = json

                for (i in 1..25) {
                    val json1 = k.substringAfter("\"subreddit\": \"")
                    k = json1
                    author = json1.substringBefore("\"")
                    auth.add(author)
                }

            }
        }


        //Поиск времени создания публикации в .json
        class MyThread2 : Thread() {
            override fun run() {

                var times : String? = null
                val json = URL(web).readText()
                var k = json

                for (i in 1..25) {
                    val json1 = k.substringAfter("\"created\": ")
                    k = json1
                    times = json1.substringBefore(".")
                    val unixTime = System.currentTimeMillis() / 1000L           //Конвертация текущего времени в Unix-формат
                    val l = times.toLong()
                    val time = (unixTime - l).toDouble()
                    val t = (round(time / 3600) + 7).toInt()                //Расчет времени с учетом местного
                    date.add(t.toString())
                }

            }
        }

        //Поиск комментариев публикации в .json
        class MyThread3 : Thread() {
            override fun run() {

                var comments : String? = null
                val json = URL(web).readText()
                var k = json

                for (i in 1..25) {
                    val json1 = k.substringAfter("\"num_comments\": ")
                    k = json1
                    comments = json1.substringBefore(",")

                    if (comments.toInt() > 1000) {
                        comments = "%.1f".format(comments.toDouble()/1000).toString()+"k"        //Округление, если >1000
                    }

                    comm.add(comments)
                }

            }
        }

        //Поиск thumbnails публикации в .json
        class MyThread4 : Thread() {
            override fun run() {
                var bmpUrl : String? = null
                var bmp : Bitmap? = null
                val json = URL(web).readText()
                var k = json
                for (i in 1..25) {
                    val json1 = k.substringAfter("\"thumbnail\": \"")
                    k = json1
                    bmpUrl = json1.substringBefore("\"")
                    if (bmpUrl != "default" && bmpUrl != "self") {
                        val input: InputStream = URL(bmpUrl).openStream()
                        bmp = BitmapFactory.decodeStream(input)
                        thumbnail.add(bmp)
                    } else {
                        val input: InputStream = URL("https://2img.net/i/default.png").openStream()
                        bmp = BitmapFactory.decodeStream(input)
                        thumbnail.add(bmp)
                    }
                }
            }
        }

        //Поиск ссылок на публикации в .json
        class MyThread5 : Thread() {
            override fun run() {

                var contUrl : String? = null
                val json = URL(web).readText()
                var k = json
                var bit : Bitmap? = null


                for (i in 1..25) {
                    val json1 = k.substringAfter(" \"url\": \"")
                    k = json1
                    contUrl = json1.substringBefore("\"")
                    content.add(contUrl)
                    val temp = contUrl?.substring(contUrl.length - 3);
                    if (temp == "jpg" || temp == "png") {
                        val input: InputStream = URL(contUrl).openStream()
                        bit = BitmapFactory.decodeStream(input)
                        thumbnail[i-1] = bit
                    }

                }
            }
        }


        val thread: Thread = MyThread()
        thread.start()
        thread.join()
        val thread2: Thread = MyThread2()
        thread2.start()
        thread2.join()
        val thread3: Thread = MyThread3()
        thread3.start()
        thread3.join()
        val thread4: Thread = MyThread4()
        thread4.start()
        thread4.join()
        val thread5: Thread = MyThread5()
        thread5.start()
        thread5.join()



        //Оформление RecyclerView через Адаптер
        var adapter: Adapter? = null
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(this, auth, date, comm, thumbnail, content)
        adapter.setClickListener(this)
        recyclerView.adapter = adapter


        progressBar.visibility = ProgressBar.INVISIBLE


    } else {
        Toast.makeText(this@MainActivity, "No Internet Connection", Toast.LENGTH_LONG).show()
        finishAffinity()
    }
}

    override fun onItemClick(view: View?, position: Int) {

    }


}