package com.kuroda33.vestibularrehabilitation
import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Size
import java.util.*
import android.hardware.camera2.CameraAccessException
import android.widget.Toast
import android.hardware.camera2.CameraCaptureSession
import java.util.Arrays.asList
import android.hardware.camera2.CameraDevice
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_camera.*
import kotlin.math.log
import kotlin.math.sin

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class CameraActivity : AppCompatActivity() , ActivityCompat.OnRequestPermissionsResultCallback, SurfaceHolder.Callback {

    var ww:Int=0
    var wh:Int=0
    var bw:Long=0//band width
    var bd:Long=0//band distance
    var rw:Long=0//rect width height
    var rectnum:Int=0//=((ww/2)/rw).toInt()*2
    var rectx0:Long=0// = ww/2-rw*rectnum/2
    private val SWIPE_DISTANCE = 10
    // 最低スワイプスピード
    private val SWIPE_VELOCITY = 200

    // タッチイベントを処理するためのインタフェース
    private var mGestureDetector: GestureDetector? = null
    var mode:Int = 0
    var backColor:Int=0
    var furi:Float=1f
    var furiy:Float=1f
    var speed:Float=1f
 //   var csize:Float=0f
    var oknreftright:Int=0
    var oknwidth:Int=0
    var oknspeed:Int=0
    var saccadePatern:Int = 0
    var saccadeSpeed:Int =0
    private var msTimer: Timer? = null
    private var sTimer: Timer? = null
    val paintred: Paint = Paint()
    val paintblack: Paint = Paint()
    val paintwhite: Paint = Paint()
    val paintgray:Paint=Paint()
    var cnt:Int=0
    var time0:Long=0
    private var mHandler = Handler()
    var KeepScreenOn:Boolean=true
    private lateinit var textureView: TextureView
    private var captureSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private var imageReader: ImageReader? = null
    private lateinit var previewRequest: CaptureRequest
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null

    var forArray = arrayListOf<Int>()//fore
    var bakArray = arrayListOf<Int>()//back
    var sndArray = arrayListOf<String>()//sound
    var timArray = arrayListOf<Int>()//start time
    var remArray = arrayListOf<Int>()//remain time
    var numArray = arrayListOf<Int>()//disp num
    val pursuitMode:Int = 1
    val oknMode:Int=2
    val saccadeMode:Int=3
    var ovalw:Long=0
    var oval:ImageView?=null
    var ovalnum:Int=0
    private var mTimer: Timer? = null
    private val previewSize: Size = Size(1, 1) // FIXME: for now.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mGestureDetector = GestureDetector(this, mOnGestureListener) // => 忘れない

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        paintred.isAntiAlias = false
        paintred.style = Paint.Style.FILL
        paintred.color = Color.RED
        paintblack.isAntiAlias = false
        paintblack.style = Paint.Style.FILL
        paintblack.color = Color.BLACK
        paintwhite.isAntiAlias = false
        paintwhite.style = Paint.Style.FILL
        paintwhite.color = Color.WHITE
        paintgray.isAntiAlias = false
        paintgray.style = Paint.Style.FILL
        paintgray.color = Color.GRAY

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        oval1.alpha=0f
        oval2.alpha=0f
        oval3.alpha=0f
        oval4.alpha=0f
        if(width<=1100){
            oval=oval1
            ovalw=20//circle:40
            ovalnum=1
        }else if(width<1300){
            oval=oval2
            ovalw=25//50
            ovalnum=2
         }else if(width<2000){
            oval=oval3
            ovalw=37//74
            ovalnum=3
         }else{
            oval=oval4
            ovalw=50//100
            ovalnum=4
        }
        oval?.x=width/2f
        oval?.y=height/2f
        //Log.d("kuroda::",width.toString()+"   "+ovalw.toString())
        val rehaList=intent.getIntegerArrayListExtra("rehaList")
        var rehaEnd=rehaList.count()/6-1
        timArray.add(1)//0.5s後に開始
        forArray.add(0)
        bakArray.add(0)
        sndArray.add("0")
        numArray.add(0)
        timArray.add(1)//さらに0.5s後に開始
              for(i:Int in 0..rehaEnd) {
            forArray.add(rehaList[i * 6])
            bakArray.add(rehaList[i * 6 + 1] % 10)
            sndArray.add(rehaList[i * 6 + 2].toString())
                  //timArrayは一つ後ろにズレている
            timArray.add(rehaList[i * 6 + 3] + timArray.last())
            //          Log.d("kuroda",timArray[i].toString())
            if (rehaList[i * 6 + 4]  != 0){//休止時間
                forArray.add(0)
                bakArray.add(1)
                sndArray.add("none")
                timArray.add(rehaList[i*6+4] + timArray.last())
                numArray.add(0)
            }
            numArray.add(rehaList[i*6+5])
        }
        forArray.add(0)
        bakArray.add(0)
        sndArray.add(0.toString())
        numArray.add(0)
        backColor=0//取りあえず
        //2s後に終了
//        forArray.add(0)
//        bakArray.add(0)
//        sndArray.add("to toppage")
//        timArray.add(2*2+timArray.last())//2秒後に戻る
        for(i:Int in 0..(timArray.count()-1)){
            Log.d("kuroda",timArray[i].toString())
        }

        msTimer = Timer()
        KeepScreenOn=true
        // タイマーの始動
        var holder = surfaceView.holder
        holder.addCallback(this)

 //       holder.setFormat(PixelFormat.TRANSLUCENT)
        val time0=System.currentTimeMillis()


        msTimer!!.schedule(object : TimerTask() {
            override fun run() {
                val temp=System.currentTimeMillis()-time0
                drawCanvas(temp)
            }
        }, 100, 16)

        textureView = findViewById(R.id.mySurfaceView)
        textureView.surfaceTextureListener = surfaceTextureListener;
        textureView.alpha=0f
        startBackgroundThread()

        sTimer = Timer()
        cnt=0
        rehaEnd=timArray.count()-1
        sTimer!!.schedule(object : TimerTask() {
            override fun run() {
                for(i:Int in 0..rehaEnd){
                    if(cnt==timArray[i]) {
                        setParam(i)
                        drawCanvasBack()
                     //   setOval(0f)
                        if (i == rehaEnd) {
                            finish()//遷移するとscreen_keep_onフラグはリセットされる
                        }
                    }
                }
                if(mode==saccadeMode){

                    if(saccadeSpeed==2){//0.5s
                        mHandler.post{
                            drawSaccade(cnt)
                            setOval(1f)
                        }
                    }else{//1.0s
                        if(cnt%2==0){
                            mHandler.post{
                                drawSaccade(cnt/2)
                                setOval(1f)
                            }
                        }
                    }
                    //setOval(1f)//ここに置くと、drawSaccadesが終わる前に実行してしまう。
                }
                cnt += 1
            }
        }, 100, 500)
    }

    var lastp:Int=0
    private fun drawSaccade(time:Int) {//saccade
        val x0=ww.toFloat()/2f-ovalw
        val y0=wh.toFloat()/2f-ovalw
        oval?.y=y0
        if(saccadePatern==1) {
            oval?.x=x0 + furi * (time % 5 - 2)
        }else if(saccadePatern==2){
            oval?.x=x0 - furi * (time % 5 - 2)
        }else if(saccadePatern==3){
            oval?.x=x0 + furi *2* (time % 3 - 1)
        }else if(saccadePatern==4){
            oval?.x=x0 - furi *2* (time % 3 - 1)
        }else if(saccadePatern==5){
            var mul:Float=0f
            if(time%2==0)mul=0f
            else if((time+1)%4==0)mul=-1f
            else mul=1f
            oval?.x=x0 - furi *2*mul
        }else if(saccadePatern==6){
            var mul:Float=0f
            if(time%2==0)mul=0f
            else if((time+1)%4==0)mul=-1f
            else mul=1f
            oval?.x=x0
            oval?.y=y0 - furiy *2* mul
        }else if(saccadePatern==7){
            val rand=Random()
            var raInt=rand.nextInt(3)
            if(raInt==lastp){
                raInt += 1
                if(raInt==3)raInt=0
            }
            oval?.x=x0 + furi *2* (raInt-1)
            lastp=raInt
        }else if(saccadePatern==8){
            val rand=Random()
            var raInt=rand.nextInt(3)
            if(raInt==lastp){
                raInt += 1
                if(raInt==3)raInt=0
            }
            oval?.x=x0
            oval?.y= y0 + furiy *2* (raInt-1)
            lastp=raInt
        }else if(saccadePatern==9){
            val rand=Random()
            var raInt=rand.nextInt(9)
            if(raInt==lastp){
                raInt += 1
                if(raInt==9)raInt=0
            }
            oval?.x=x0+ furi *2* (raInt%3-1)
            oval?.y= y0 + furiy *2* (raInt/3-1)
            lastp=raInt
        }
    }
    var ovalf:Float =-1f
    private fun setOval(f:Float){
        if(ovalf==f)return
        ovalf=f
        if(ovalnum==1){
            oval1.alpha=f
        }else if(ovalnum==2){
            oval2.alpha=f
        }else if(ovalnum==3){
            oval3.alpha=f
        }else{
            oval4.alpha=f
        }
    }
    private fun setParam(i:Int){
        val fore=forArray[i]
        backColor=bakArray[i]
        mode=pursuitMode
        setOval(0f)
        if(fore==3){
            furi=ww.toFloat()*6.0f/20.0f
            speed=3.1415f/1666f
        }else if(fore==6){
            furi=ww.toFloat()*6.0f/20.0f
            speed=2f*3.1415f/1666f
        }else if(fore==9){
            furi=ww.toFloat()*9.0f/20.0f
            speed=3f*3.1415f/1666f
        }else if(fore==2){//Fixed
            furi=0f
            speed=0f
        }else if(fore>99){//OKN
            oknreftright=fore/100
            oknwidth=(fore/10)%10
            oknspeed=fore%10
            if(oknwidth==1) {
                bw = (ww / 10).toLong()
                bd = (ww / 5).toLong()
            }else{
                bw = (ww / 20).toLong()
                bd = (ww / 10).toLong()
            }
            mode=oknMode
        }else if(fore>9){
            mode=saccadeMode
            saccadeSpeed=fore%10
            saccadePatern=fore/10
            furi=ww.toFloat()*9.0f/40.0f
            furiy=wh.toFloat()*9.0f/40.0f
        }else if(fore==0){
            mode=0
        }
    }
    var lastBack:Int=-1
    private fun drawCanvas(time:Long) {//pursuit,okn
        if(mode==saccadeMode)return
        if(mode==oknMode){
            val canvas = surfaceView.holder.lockCanvas()
            if(ww==0||canvas==null)return
            var torg = 1L
            if (oknspeed == 1) torg = time / 3
            else torg = time * 2 / 3
            torg=torg*ww/1024//amazon KFAUWI(width:1024)を基準
            var bandn:Int=4
            if(oknwidth==2)bandn=9
//            canvas.drawRect(0f, 0f,ww.toFloat(),wh.toFloat(), paintwhite)
            canvas.drawColor(Color.WHITE)
            if(oknreftright==2) {
                for (i in 0..bandn) {
                    var xs = (i * bd + torg) % ww
                    canvas.drawRect(xs.toFloat(), 0f, xs.toFloat() + bw.toFloat(), wh.toFloat(), paintblack)
                    if (xs + bw > ww) {
                        xs = xs + bw - ww
                        canvas.drawRect(0f, 0f, xs.toFloat(), wh.toFloat(), paintblack)
                    }
                }
            }else{
                for (i in 0..bandn) {
                    var xs = (i * bd + torg) % ww
                    canvas.drawRect((ww-xs).toFloat(), 0f, (ww-xs).toFloat() + bw.toFloat(), wh.toFloat(), paintblack)
                    if (ww-xs+bw >ww ) {
                        xs = bw-xs
                        canvas.drawRect(0f, 0f, xs.toFloat(), wh.toFloat(), paintblack)
                    }
                }
            }
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }else if(mode==pursuitMode){
            mHandler.post{
                oval?.x=ww.toFloat() / 2f -ovalw+ furi * sin(speed * time.toFloat())
                oval?.y=wh.toFloat() / 2f -ovalw
                if(mode==pursuitMode)setOval(1f)//ややこしい？スレッドだから？
                //pursuitMode以外のときもここを通っている
            }
        }
      }
    private fun getx0(x:Float) :Float{
        if(x<0)return 0f
        else return x
    }
    private fun drawCanvasBack() {//pursuit,okn
        if(mode==oknMode)return
 //       if(mode==saccadeMode)return
        Log.d("BACK:",backColor.toString())
        val canvas = surfaceView.holder.lockCanvas()
        if(ww==0||canvas==null)return
        if(backColor==0){
            canvas.drawRect(0f, 0f,ww.toFloat(),wh.toFloat(), paintwhite)
            lastBack=0
            textureView.alpha=0f
        }
        else if(backColor==1){
            canvas.drawRect(0f, 0f,ww.toFloat(),wh.toFloat(), paintgray)
//            canvas.drawColor(Color.GRAY)
            lastBack=1
            textureView.alpha=0f

        }else if(backColor==3&&lastBack!=3) {
            canvas.drawRect(0f, 0f,ww.toFloat(),wh.toFloat(), paintwhite)
//            canvas.drawColor(Color.WHITE)
            textureView.alpha = 1.0f
            lastBack = 3
        }else if(backColor==2) {
            lastBack=2
            textureView.alpha=0f
            for(i in 0..(rectnum+1) ){
                val ww=i.toFloat()*rw
                val w0 = rectx0 - rw
                for(j in 0..9){
                    if((i+j)%2==0)canvas.drawRect(getx0(w0+ww), rw*j.toFloat(),w0 + ww + rw,rw*(j+1).toFloat(), paintblack)
                    else canvas.drawRect(getx0(w0+ww), rw*j.toFloat(),w0 + ww + rw,rw*(j+1).toFloat(), paintwhite)
                }
            }
        }
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
    fun openCamera(width:Int,height:Int) {
        var manager: CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            var camerId: String = manager.getCameraIdList()[0]
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission()
                return
            }
            manager.openCamera(camerId, stateCallback, null);
            configureTransform(width, height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        ww=width
        wh=height
        rw=height/10L
        rectnum=((ww/2)/rw).toInt()*2
        rectx0 = ww/2-rw*rectnum/2
    }
    override fun onDestroy() {
        super.onDestroy()
//        if(cameraDevice != null) {
//            cameraDevice = null
//        }

        if (sTimer != null){
            sTimer!!.cancel()
            sTimer = null
        }
        if (msTimer != null){
            msTimer!!.cancel()
            msTimer = null
        }
       // setOval(0f)
        captureSession?.close()

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }
//
    override fun surfaceCreated(holder: SurfaceHolder?) {
    }
    private val stateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(cameraDevice: CameraDevice) {
            this@CameraActivity.cameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@CameraActivity.cameraDevice = null
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            onDisconnected(cameraDevice)
            finish()
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val texture = textureView.surfaceTexture
            texture.setDefaultBufferSize(previewSize.width, previewSize.height)
            val surface = Surface(texture)
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilder.addTarget(surface)
            cameraDevice?.createCaptureSession(Arrays.asList(surface, imageReader?.surface),
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {

                        if (cameraDevice == null) return
                        captureSession = cameraCaptureSession
                        try {
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            previewRequest = previewRequestBuilder.build()
                            captureSession?.setRepeatingRequest(previewRequest,
                                null, Handler(backgroundThread?.looper))
                        } catch (e: CameraAccessException) {
                            Log.e("erfs", e.toString())
                        }
                    }
                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        //Tools.makeToast(baseContext, "Failed")
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e("erf", e.toString())
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            AlertDialog.Builder(baseContext)
                .setMessage("Permission Here")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(arrayOf(Manifest.permission.CAMERA),
                        200)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    finish()
                }
                .create()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 200)
        }
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation
        //       activity ?: return
        //       val rotation = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                viewHeight.toFloat() / previewSize.height,
                viewWidth.toFloat() / previewSize.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        textureView.setTransform(matrix)
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        //      backgroundHandler = Handler(backgroundThread?.looper)
    }


    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            imageReader = ImageReader.newInstance(width,height,ImageFormat.JPEG, 2)
            openCamera(width,height)
        }

        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
            //          configureTransform(p1,p2)
            //       cnt1 += 1
            // Log.e("UPDATE:", cnt1.toString())
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
            //   cnt1 += 1
            //Log.e("UPDATE:", cnt1.toString())
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            return false
        }
    }
    fun nextScene(){
        val max=timArray.count()
        for(i in 0..(max-1)){
            if(cnt>timArray[i]&&cnt<timArray[i+1]){
                cnt=timArray[i+1]
            }
        }
    }
    fun backScene(){
        val max=timArray.count()
        for(i in 0..(max-1)){
            if(cnt>timArray[i] && cnt<timArray[i+1]+5){
                cnt=timArray[i]
            }else if(cnt>timArray[6]) {
                cnt=timArray[6]
            }
        }

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector!!.onTouchEvent(event)
    }
    // タッチイベントのリスナー
    private val mOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        // フリックイベント
        override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            try {
                // 移動距離・スピードを出力
                //              val distance_y = Math.abs(event1.y - event2.y)
                //              val velocity_y = Math.abs(velocityY)
                //          val distance_x = Math.abs(event1.x - event2.x)
                //          val velocity_x = Math.abs(velocityX)

                if (event2.x - event1.x > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                    Log.d("onFling","右へ")
                    backScene()
                    // 終了位置から開始位置の移動距離が指定値より大きい
                    // Y軸の移動速度が指定値より大きい
                } else if (event1.x - event2.x > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                    Log.d("onFling","左へ")
                    nextScene()
                }

            } catch (e: Exception) {
                // TODO
            }
            return false
        }
    }
}
