package com.jzhao.pixelartconverter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*
import kotlin.math.floor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jzhao.pixelartconverter.adapter.ColorImageAdapter
import com.jzhao.pixelartconverter.adapter.ColorPaletteAdapter
import com.jzhao.pixelartconverter.adapter.GridBitAdapter
import com.jzhao.pixelartconverter.adapter.GridSpaceItemDecoration
import com.jzhao.pixelartconverter.model.ColorImageListViewModel
import com.jzhao.pixelartconverter.model.ColorPaletteListViewModel
import kotlin.math.pow
import kotlin.math.sqrt


const val IMAGE_PICK_CODE = 1000
const val START_PROCESSING_CODE = 1001
var ORIGINAL_IMAGE_URI:Uri = Uri.EMPTY
var selectedImage = false

class MainActivity : AppCompatActivity() {
    lateinit var decorView: View
    private var uiOption = 0
    private lateinit var colorPaletteAdapter: ColorPaletteAdapter
    private lateinit var colorImageAdapter: ColorImageAdapter
    private lateinit var gridBitAdapter: GridBitAdapter
    var bitArray = ArrayList<String>()
    var with =0
    var height =0
    var wiasdf = 0

    private val colorImageListViewModel: ColorImageListViewModel by lazy {
        ViewModelProvider(this).get(ColorImageListViewModel::class.java)
    }

    private val colorPaletteListViewModel: ColorPaletteListViewModel by lazy {
        ViewModelProvider(this).get(ColorPaletteListViewModel::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        decorView = window.decorView
        uiOption = window.decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) uiOption =
            uiOption or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.setSystemUiVisibility(uiOption)

        setColorPaletteRecycleView()
        setColorImageRecycleView()
    }

    /** Called when the user taps the Send button */
    fun pickFromGallery(view: View) {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
        imageView2.visibility = View.INVISIBLE
    }


    //handle result of picked image
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if(data != null){
                ORIGINAL_IMAGE_URI = data.data!!
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(ORIGINAL_IMAGE_URI)
                var original = (imageView.drawable as BitmapDrawable).bitmap
                val options = RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                wiasdf= original.width/4
                GlideApp.with(this).load(ORIGINAL_IMAGE_URI).apply(options).listener(glide_lis()).override(original.width/4, original.height/4).into(imageView)

                //

            }
        }
    }

    fun glide_lis() : RequestListener<Drawable> {
        return object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                Log.v("ddsf" , "실패")
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                Log.v("ddsf" , "성공")
                selectedImage = true
                var original = (resource as BitmapDrawable).bitmap
                imageView.setImageBitmap(original)
                //높이가 더 클때
                if(original.height >= original.width ){
                    height = 64
                    with = ((original.width * 64)/original.height)
                    //   HorizontalPixelCount.setText(with.toString())
                    //   VerticalPixelCount.setText(height.toString())
                }else if(original.width >= original.height){
                    with = 64
                    height = ((original.height * 64)/original.width)
                    //  HorizontalPixelCount.setText(with.toString())
                    //  VerticalPixelCount.setText(height.toString())
                }
                return true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startProcessing(view: View) {

        if(!selectedImage){
            toast("Please select an image!")
            return
        }
        var horiPixelCount = with
        var vertiPixelCount = height
        var original = (imageView.drawable as BitmapDrawable).bitmap

        var originalPixelsArr: IntArray = IntArray(original.height * original.width)

        Log.v("ssss" ,     ((original.width * 32)/original.height).toString())

        original.getPixels(originalPixelsArr, 0, original.width, 0, 0, original.width, original.height)

        val pixelWidth = floor((original.width / horiPixelCount.toDouble())).toInt()
        val pixelHeight = floor((original.height / vertiPixelCount.toDouble())).toInt()

        var pixelArt = Bitmap.createBitmap(pixelWidth * horiPixelCount, pixelHeight * vertiPixelCount, Bitmap.Config.ARGB_8888, true)
        pixelArt.eraseColor(Color.WHITE)
        // createPaletteAsync(pixelArt)
        //printing original over pixel art's canvas

        print(pixelArt.width)
        print(original.width)
        pixelArt.setPixels(originalPixelsArr, 0, original.width, 0,0, pixelArt.width, pixelArt.height)
        updateColorImageRecycleViewer(pixelArt) // 팔레트 색 채우기
        var i = 0
        var ii = 0
        while(i < vertiPixelCount){
            ii = 0
            while(ii < horiPixelCount){
                var A: Long = 0
                var R: Long = 0
                var G: Long = 0
                var B: Long = 0

                var x = 0
                var y = 0
                while(y < pixelHeight){
                    x = 0
                    while(x < pixelWidth){
                        var curPixelColor = pixelArt.getPixel(ii * pixelWidth + x, i * pixelHeight + y)
                        A += Color.alpha(curPixelColor)
                        R += Color.red(curPixelColor) as Int
                        G += Color.green(curPixelColor) as Int
                        B += Color.blue(curPixelColor) as Int
                        x++
                    }
                    y++
                }


                A /= pixelHeight * pixelWidth
                R /= pixelHeight * pixelWidth
                G /= pixelHeight * pixelWidth
                B /= pixelHeight * pixelWidth


                var a = A.toInt()
                var r = R.toInt()
                var g = G.toInt()
                var b = B.toInt()

                if(!original.hasAlpha()){
                    a = 255
                }
                

                //https://aroundck.tistory.com/292 비트맵을 캔버스에 옮겨서 거기에 텍스트 그리기
                var newPixelColor = Color.argb(a, r, g, b) // 이색이랑 팔레트 색이랑 비교해서 숫자 넣어야함.
                bitArray.add(calculeColor(newPixelColor).toString())// 이렇게 계산해서 젤 숫자가 작은게 유사도가 높은거일테니까
                var whiteColor = Color.argb(255,255,255,255)
                x = 0
                y = 0
                while(y < pixelHeight){
                    x = 0
                    while(x < pixelWidth){
                        pixelArt.setPixel(ii * pixelWidth + x, i * pixelHeight + y, newPixelColor)
                        x++
                    }
                    y++
                }

                x = 0
                y = 0

                ///검은선 넣기
                /*
                while(y < pixelHeight){
                    pixelArt.setPixel(ii * pixelWidth, i * pixelHeight + y, Color.BLACK)
                    y++
                }
                while(x < pixelWidth){
                    pixelArt.setPixel(ii * pixelWidth + x, i * pixelHeight, Color.BLACK)
                    x++
                }*/
                //검은선 넣기 끝
                ii++
            }
            i++
        }

        setbitGridRecyclerView(bitArray,with) // 색계산 다 끝나고 with가 정해지고 나서 그리드를 만든다.
        var savedImageURI = saveImage(pixelArt, "PixelArt_${UUID.randomUUID()}")
        imageView2.setImageURI(savedImageURI)
        imageView2.visibility = View.VISIBLE
    }

    // Method to save an image to gallery and return uri
    private fun saveImage(bitmap:Bitmap, title:String):Uri{
        // Save image to gallery
        var savedImageURL = ""
        try {
            savedImageURL = MediaStore.Images.Media.insertImage(contentResolver, bitmap, title, "Image of $title")
            toast("Image saved to " + savedImageURL)
        }catch(e: IOException){
            e.printStackTrace()
            toast("Error to save image")
        }
        // Parse the gallery image url to uri
        return Uri.parse(savedImageURL)
    }


    // Extension function to show toast message
    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Generate palette synchronously and return it
    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

    private fun setColorPaletteRecycleView() {

        colorPalette_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        colorPaletteAdapter = ColorPaletteAdapter(colorPaletteListViewModel.colorList)
        colorPalette_recycler_view.adapter = colorPaletteAdapter

    }

    fun setbitGridRecyclerView(list : ArrayList<String> , with : Int){
        bitgrid_recyclerView.layoutManager = GridLayoutManager(this , height)
        bitgrid_recyclerView.addItemDecoration(GridSpaceItemDecoration(wiasdf,0,false))
        gridBitAdapter = GridBitAdapter(list)
        bitgrid_recyclerView.adapter = gridBitAdapter
    }

    private fun setColorImageRecycleView() {

        colorImage_recycler_view.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        colorImageAdapter = ColorImageAdapter(colorImageListViewModel.colorList)
        colorImage_recycler_view.adapter = colorImageAdapter


    }


    private fun updateColorImageRecycleViewer(bitmap: Bitmap) {
        Log.v("들어왔나" , "리사일")
        extractColorsFromImage(bitmap)
        generatePalette()
        updateUI()
    }

    private fun extractColorsFromImage(bitmap: Bitmap) {
        val colorBucket = createColorBucket(bitmap)
        for (i in colorBucket.indices) {
            colorImageListViewModel.colorList[i] = colorBucket[i]
        }
    }

    private fun generatePalette() {
        val sample = Palette(
            intArrayOf(
                colorImageListViewModel.colorList[0].toArgb(),
                colorImageListViewModel.colorList[1].toArgb(),
                colorImageListViewModel.colorList[2].toArgb(),
                colorImageListViewModel.colorList[3].toArgb(),
                colorImageListViewModel.colorList[4].toArgb()
            )
        )

        for (i in colorPaletteListViewModel.colorList.indices) {
            colorPaletteListViewModel.colorList[i] = sample.generateColor()
        }
    }

    private fun createColorBucket(bitmap: Bitmap): List<Color> {
        var colorList = mutableListOf<FloatArray>()

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val color = bitmap.getPixel(x, y)
                addHsvColorToList(color, colorList)
            }
        }

        val colorExtractor = ColorExtractor(colorList, 10, 5)
        return colorExtractor.extract()
    }

    private fun addHsvColorToList(color: Int, colorList: MutableList<FloatArray>) {
        var hsvColor = FloatArray(3)
        Color.colorToHSV(color, hsvColor)
        colorList.add(hsvColor)
    }

//    fun isStoragePermissionGranted(): Boolean {
//        val TAG = "Storage Permission"
//        return if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.v(TAG, "Permission is granted")
//                true
//            } else {
//                Log.v(TAG, "Permission is revoked")
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    1
//                )
//                false
//            }
//        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG, "Permission is granted")
//            true
//        }
//    }
private fun updateUI() {
    val imageColors = colorImageListViewModel.colorList
    colorImageAdapter = ColorImageAdapter(imageColors)
    colorImage_recycler_view.adapter = colorImageAdapter

    val paletteColors = colorPaletteListViewModel.colorList
    colorPaletteAdapter = ColorPaletteAdapter(paletteColors)
    colorPalette_recycler_view.adapter = colorPaletteAdapter
}

    fun calculeColor(newPixelColor : Int) : Int{
        //RGB 모델은 두 색 (r1,g1,b1), (r2,g2, b2) 사이의 차이를 유클리디언(Euclidean)
        // 거리를 이용해서 d = sqrt{(r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2}와 같이 계산할 수 있다.
        var num = 0
        var result = 0f
        var zz = 1100f
        var result_List = ArrayList<Pair<Float, Int>>()
        var min = 0f
        for(i in 0..4){
            result = sqrt((colorImageListViewModel.colorList[i].toArgb().red.toFloat()- newPixelColor.red.toFloat()).pow(2) +
                    (colorImageListViewModel.colorList[i].toArgb().green.toFloat()- newPixelColor.green.toFloat()).pow(2)+
                    (colorImageListViewModel.colorList[i].toArgb().blue.toFloat()- newPixelColor.blue.toFloat()).pow(2))

            result_List.add(Pair(result ,i))
        }
        for(i in 0..4) {
            if(zz > result_List.get(i).first){
                zz = result_List.get(i).first
                num = i
            }
        }
        Log.v("sdfsdfsdf" , num.toString())


        return num
    }
}


