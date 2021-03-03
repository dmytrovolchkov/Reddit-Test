package com.volchkov.reddittestapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Suppress("DEPRECATION")
class Adapter internal constructor(
    context: Context?,
    data1: List<String>,
    data2: List<String>,
    data3: List<String>,
    data4: List<Bitmap>,
    data5: List<String>
) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    private val mData1: List<String> = data1
    private val mData2: List<String> = data2
    private val mData3: List<String> = data3
    private val mData4: List<Bitmap> = data4
    private val mData5: List<String> = data5
    private var mClickListener: ItemClickListener? = null
    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    // Расширение recyclerview_row из xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_row, parent, false)
        return ViewHolder(view)
    }

    // Связь данных с Views
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val auth = mData1[position]
        val dat = mData2[position]
        val comm = mData3[position]
        val thum = mData4[position]
        val cont = mData5[position]
        holder.myTextView1.text = ("Author: $auth")
        holder.myTextView2.text = ("Time:  $dat hours ago")
        holder.myTextView3.text = ("Comments:  $comm")
        holder.myImageView.setImageBitmap(thum)
        holder.myImageView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(cont))
            holder.myImageView.context.startActivity(browserIntent)
        }
        holder.myButton.setOnClickListener {

            var k = mData5[position].substring(mData5[position].length - 3);

            if (k == "jpg" || k == "png") {
                fun SavePicture(folderToSave: String): String? {
                    var fOut: OutputStream? = null
                    try {
                        val file = File(folderToSave, "SavedImage$position.jpg") // Создание имени файла
                        fOut = FileOutputStream(file)
                        mData4[position].compress(Bitmap.CompressFormat.JPEG, 100, fOut) // Сохранение картинки в jpeg-формате с 100% сжатия.
                        fOut.flush()
                        fOut.close()
                        // регистрация в фотоальбоме
                        MediaStore.Images.Media.insertImage(holder.myImageView.context.contentResolver, file.absolutePath, file.name, file.name)
                    } catch (e: Exception)
                    {
                        return e.message
                    }
                    return ""
                }
                SavePicture(holder.myImageView.context.filesDir.toString())
                Toast.makeText(holder.myImageView.context, "Image Saved", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(holder.myImageView.context, "No Picture Available", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return mData1.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var myTextView1: TextView = itemView.findViewById(R.id.author)
        var myTextView2: TextView = itemView.findViewById(R.id.time)
        var myTextView3: TextView = itemView.findViewById(R.id.comment)
        var myImageView: ImageView = itemView.findViewById(R.id.image)
        var myButton: Button = itemView.findViewById(R.id.button)
        override fun onClick(view: View?) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

    // Перехват нажатий
    fun setClickListener(itemClickListener: MainActivity) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}

