package com.amir.stickergram.infrastructure

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*

import androidx.exifinterface.media.ExifInterface

import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog

import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.amir.stickergram.CropActivity
import com.amir.stickergram.EditImageActivity
import com.amir.stickergram.mode.Mode
import com.amir.stickergram.R
import com.amir.stickergram.base.BaseActivity
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.ArrayList
import java.util.Locale

import app.minimize.com.seek_bar_compat.SeekBarCompat

import com.amir.stickergram.image.TextItem
import com.amir.stickergram.image.TouchImageView

object Loader {
    private const val TAG = "LOADER"

    val activeStickerDir: String
        get() = BaseActivity.chosenMode.cacheDir

    val activePack: String?
        get() = BaseActivity.chosenMode.pack
    
    fun gainPermission(activity: BaseActivity, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //    public static void gainPermissionAnthon(BaseActivity activity, int requestCode) {
    //        if (
    //                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
    //                        Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
    //
    //            // Should we show an explanation?
    //            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
    //                    Manifest.permission.READ_CONTACTS)) {
    //
    //                // Show an explanation to the user *asynchronously* -- don't block
    //                // this thread waiting for the user's response! After the user
    //                // sees the explanation, try again to request the permission.
    //
    //            } else {
    //
    //                // No explanation needed, we can request the permission.
    //
    //                ActivityCompat.requestPermissions(activity,
    //                        new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
    //
    //                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
    //                // app-defined int constant. The callback method gets the
    //                // result of the request.
    //            }
    //        }
    //    }
    //
    //    public static boolean checkPermissionAnthon(BaseActivity activity) {
    //        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
    //            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    //        return true;
    //    }

    fun checkPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED else true
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File): Boolean {
        if (!destFile.parentFile!!.exists())
            if (destFile.parentFile!!.mkdirs())
                Log.e(TAG, "couldn't make parent")

        if (!destFile.exists()) {
            if (!destFile.createNewFile())
                Log.e(TAG, "couldn't make file")
            //                return false;
        } else {
            destFile.delete()
            destFile.createNewFile()
        }

        var source: FileChannel? = null
        var destination: FileChannel? = null

        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination!!.transferFrom(source, 0, source!!.size())
        } finally {
            source?.close()
            destination?.close()
        }


        return true
    }

    fun generateThumbnail(fromDirectory: String, toDirectory: String): String? {
        val regionalBitmap = BitmapFactory.decodeFile(fromDirectory) ?: return null
//        if (regionalBitmap == null) Log.e(TAG, "regionalBitmap was null");
        val bitmap = ThumbnailUtils.extractThumbnail(regionalBitmap, regionalBitmap.width / 4, regionalBitmap.height / 4)
        var outStream: FileOutputStream? = null

        try {
            outStream = FileOutputStream(toDirectory)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, outStream)
                return toDirectory
            } else {
                return null
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                outStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun loadStickerDialog(uri: Uri, activity: BaseActivity) {

        val listener = DialogInterface.OnClickListener { dialog, which ->
            if (which == Dialog.BUTTON_POSITIVE) {
                val intent = Intent(activity, EditImageActivity::class.java)
                intent.putExtra(Constants.EDIT_IMAGE_URI, uri)
                // TODO: Animation
                activity.startActivity(intent)
                activity.finish()
            }
        }

        val view = activity.layoutInflater.inflate(R.layout.dialog_single_item, null, false)
        activity.setFont(view as ViewGroup)

        val textView = view.findViewById<View>(R.id.dialog_single_item_title) as TextView
        textView.text = activity.getString(R.string.edit_this_sticker)

        val stickerImage = view.findViewById<View>(R.id.dialog_single_item_image) as ImageView

        Log.e(TAG, "uri: $uri")
        stickerImage.setImageURI(uri)

        val dialog = AlertDialog.Builder(activity)
                .setView(view)
                //                .setTitle(activity.getString(R.string.edit_this_sticker))
                .setNegativeButton(activity.getString(R.string.no), listener)
                .setPositiveButton(activity.getString(R.string.yes), listener)
                .create()

        dialog.setOnShowListener {
            activity.setFont(dialog.findViewById<View>(android.R.id.message) as TextView?)
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_NEGATIVE))
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_NEUTRAL))
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_POSITIVE))
        }

        dialog.window!!.attributes.width = ActionBar.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes.height = ActionBar.LayoutParams.MATCH_PARENT

        dialog.show()
    }

    //    public static void loadStickerDialog(final PackItem item, final BaseActivity activity) {
    //
    //        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                if (which == Dialog.BUTTON_POSITIVE) {
    //                    Intent intent = new Intent(activity, EditImageActivity.class);
    //                    intent.putExtra(BaseActivity.EDIT_IMAGE_DIR_IN_ASSET, item.getDirInAsset());
    //                    //// TODO: Animation
    //                    activity.startActivity(intent);
    //                    activity.finish();
    //                }
    //            }
    //        };
    //
    //        View view = activity.getLayoutInflater().inflate(R.layout.dialog_single_item, null, false);
    //        ImageView stickerImage = (ImageView) view.findViewById(R.id.dialog_single_item_image);
    //
    //        Bitmap bitmap = null;
    //        try {
    ////            InputStream inputStream = item.getInputStream();
    //            bitmap = BitmapFactory.decodeStream(inputStream);
    //            if (inputStream != null) inputStream.close();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //        if (stickerImage != null)
    //            stickerImage.setImageBitmap(bitmap);
    //        else Log.e("Loader", "dialog_single_item_image was null");
    //
    //        AlertDialog dialog = new AlertDialog.Builder(activity)
    //                .setView(view)
    //                .setTitle(activity.getString(R.string.edit_this_sticker))
    //                .setNegativeButton(activity.getString(R.string.no), listener)
    //                .setPositiveButton(activity.getString(R.string.yes), listener)
    //                .create();
    //
    //        dialog.show();
    //    }

    fun saveBitmapToCache(mainBitmap: Bitmap) {
        var outputStream: OutputStream
        val file = File(BaseActivity.TEMP_STICKER_CASH_DIR)
        try {
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            outputStream = FileOutputStream(file)
            outputStream.close()
            var inputStream: InputStream
            var i = 0
            do {
                val temp = reduceImageSize(mainBitmap, i)
                outputStream = FileOutputStream(file)
                temp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                inputStream = FileInputStream(file)
                Log.e("fileSize: $TAG", inputStream.available().toString())
                i++
            } while (inputStream.available() >= 350000) // decreasing size to please the Telegram
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun reduceImageSize(mainBitmap: Bitmap, i: Int): Bitmap {
        val height = mainBitmap.height
        val width = mainBitmap.width
        Log.e(TAG, (1 + i / 10.0).toString())
        val temp = Bitmap.createScaledBitmap(mainBitmap, (width / (1 + i / 10.0)).toInt(), (height / (1 + i / 10.0)).toInt(), false)
        return Bitmap.createScaledBitmap(temp, width, height, false)
    }

    @Throws(IOException::class)
    fun makeACopyToFontFolder(uri: Uri, activity: BaseActivity): String? {
        //        Log.e(TAG, "fileName: " + getFileName(uri, activity));
        val fileName = getFileName(uri, activity)
        if (!fileName.toLowerCase().contains(".ttf")) {
            Toast.makeText(activity, activity.getString(R.string.choose_a_font), Toast.LENGTH_LONG).show()
            return null
        }
        val file = File(Constants.FONT_DIRECTORY + getFileName(uri, activity))// you can also use app's internal cache to store the file
        if (!file.parentFile!!.exists()) {
            if (!file.parentFile!!.mkdirs())
                return null
        }

        if (!file.exists()) {
            if (!file.createNewFile()) {
                return null
            }
        } else {
            if (!file.delete())
                if (!file.createNewFile())
                    return null
        }

        val fos = FileOutputStream(file)
        //        Log.e(TAG, "copying");
        val `is` = activity.contentResolver.openInputStream(uri) ?: return null
        //        Log.e(getClass().getSimpleName(), String.valueOf(uri.getQueryParameterNames()));
        val buffer = ByteArray(1024)
        var len: Int
        try {
            len = `is`.read(buffer)
            while (len != -1) {
                fos.write(buffer, 0, len)
                len = `is`.read(buffer)
            }

            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun getFileName(uri: Uri, activity: BaseActivity): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = activity.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        Log.e(TAG, "result : $result")
        return result
    }

    fun getSeekBar(context: Context,
                   range: Int,
                   backGroundColor: Int,
                   progressColor: Int,
                   thumbColor: Int,
                   defaultPosition: Int,
                   viewGroup: RelativeLayout): SeekBarCompat {
        val seekBar = SeekBarCompat(context)
        seekBar.max = range
        //        seekBar.setBackgroundColor(backGroundColor);
        seekBar.setBackgroundColor(backGroundColor)
        seekBar.setProgressColor(progressColor)
        seekBar.setThumbColor(thumbColor)
        seekBar.progress = defaultPosition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            seekBar.elevation = 5f
        }

        val scale = BaseActivity.density
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (50 * scale).toInt())
        if (!BaseActivity.isInLandscape) {
            params.addRule(RelativeLayout.ABOVE, R.id.include_buttons_scroll_view)
            //            params.setMargins((int) (10 * scale), 0, (int) (10 * scale), (int) (55 * scale));
            params.setMargins((10 * scale).toInt(), 0, (10 * scale).toInt(), (5 * scale).toInt())
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            params.addRule(RelativeLayout.START_OF, R.id.view_horizontal)
            params.setMargins((10 * scale).toInt(), 0, (10 * scale).toInt(), (10 * scale).toInt())
        }
        params.marginStart = (10 * scale).toInt()
        params.marginEnd = (10 * scale).toInt()
        seekBar.layoutParams = params
        seekBar.visibility = View.GONE
        viewGroup.addView(seekBar)

        return seekBar
    }

    fun setColor(activity: BaseActivity, touchImageView: TouchImageView, type: Int) {

        ColorPickerDialogBuilder
                .with(activity)
                .setTitle(activity.getString(R.string.choose_color))
                .initialColor(Color.WHITE)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener {
                    //                        toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                }
                .setPositiveButton(activity.getString(R.string.ok)) { dialog, selectedColor, allColors ->
                    //                        changeBackgroundColor(selectedColor);
                    if (type == Constants.TEXT_COLOR)
                        (touchImageView.drawableItem as TextItem).textColor = selectedColor
                    else if (type == Constants.TEXT_SHADOW_COLOR)
                        (touchImageView.drawableItem as TextItem).shadow.color = selectedColor
                    else if (type == Constants.TEXT_BACKGROUND_COLOR)
                        (touchImageView.drawableItem as TextItem).backgroundColor = selectedColor
                    else if (type == Constants.TEXT_STROKE_COLOR)
                        (touchImageView.drawableItem as TextItem).textStrokeColor = selectedColor
                    touchImageView.updateDrawable()
                }
                .setNegativeButton(activity.getString(R.string.cancel)) { dialog, which -> }
                .build()
                .show()
    }

    fun isPersian(string: String): Boolean {
        for (i in 0 until string.length) {
            val charAsciiNum = string[i].toInt()
            if (charAsciiNum > 1575 && charAsciiNum < 1641 || charAsciiNum == 1662 || charAsciiNum == 1711 || charAsciiNum == 1670 || charAsciiNum == 1688)
                return true
        }
        return false
    }


    fun isAppInstalled(context: Context, packageName: String?): Boolean {
        if (packageName == null) return false
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    fun capturedRotationFix(absolutePath: String): Float {
        var ei: ExifInterface? = null
        try {
            ei = ExifInterface(absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var orientation = 0
        if (ei != null) {
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        }

        //        Log.e(TAG, "orientation: " + orientation);
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270f
        }// etc.
        return 0f
    }

    fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        var source: Bitmap? = source ?: return null
        Log.e(TAG, "rotation angle: $angle")
        source = source!!.copy(Bitmap.Config.ARGB_8888, true)
        val matrix = Matrix()
        //        matrix.postRotate(angle);
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height, matrix, true)
    }

    fun getRealPathFromURI(contentURI: Uri, contentResolver: ContentResolver): String? {
        val result: String?
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun copyFile(inStream: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        try {
            var read = inStream.read(buffer)
            while (read != -1) {
                out.write(buffer, 0, read)
                read = inStream.read(buffer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun crop(source: Uri, destiny: Uri, activity: BaseActivity, isEmpty: Boolean) {
        val intent = Intent(activity, CropActivity::class.java)
        intent.putExtra(Constants.CROP_SOURCE, source)
        intent.putExtra(Constants.CROP_DESTINY, destiny)
        intent.putExtra(Constants.IS_USING_EMPTY_IMAGE, isEmpty)
        activity.startActivity(intent)
    }

    fun freeMemory(): Long {
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val free: Long
        //        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            free = statFs.availableBlocksLong * statFs.blockSizeLong / 1048576
        } else {
            free = (statFs.availableBlocks * statFs.blockSize / 1048576).toLong()
        }

        return free
        //return 100;
    }


    fun joinToStickergramChannel(activity: BaseActivity) {
        if (activePack != null) {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_TO_CHANNEL))
            myIntent.setPackage(activePack)
            activity.startActivity(myIntent)
        } else
            Toast.makeText(activity, activity.getString(R.string.telegram_is_not_installed), Toast.LENGTH_SHORT).show()
    }

    /**
     * this method is used for multiple reasons
     *
     *
     * if user choose to use an empty picture to make an sticker
     * or if we need to make a file to override as
     *
     * @return
     */

    fun generateEmptyBitmapFile(activity: BaseActivity): File {
        val tempFile = File(BaseActivity.TEMP_STICKER_CASH_DIR)
        //        else tempFile = new File(BaseActivity.TEMP_STICKER_CASH_DIR_2);
        //        else tempFile = new File(BaseActivity.TEMP_STICKER_CASH_DIR);
        try {
            val inputStream: InputStream = activity.assets.open("empty.png")
            if (tempFile.exists()) tempFile.delete()
            tempFile.createNewFile()
            val os = FileOutputStream(tempFile)
            copyFile(inputStream, os)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return tempFile
    }

    fun checkLuckyPatcher(context: Context): Boolean {
        if (isAppInstalled(context, "com.dimonvideo.luckypatcher")) {
            return true
        }

        if (isAppInstalled(context, "com.chelpus.lackypatch")) {
            return true
        }

        if (isAppInstalled(context, "com.android.vending.billing.InAppBillingService.LOCK")) {
            return true
        }

        return isAppInstalled(context, "com.android.vending.billing.InAppBillingService.LACK")

    }

    fun rate(activity: BaseActivity) {
        val appPackageName = activity.packageName
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        } else {
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            activity.startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(activity, activity.getString(R.string.no_market_was_found), Toast.LENGTH_SHORT).show()
        }

    }

    fun exit(activity: BaseActivity) {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            if (which == Dialog.BUTTON_NEGATIVE) {
                rate(activity)
            } else if (which == Dialog.BUTTON_POSITIVE) {
                activity.finish()
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity.startActivity(intent)
            } else if (which == Dialog.BUTTON_NEUTRAL) {
                joinToStickergramChannel(activity)
            }
        }
        val dialog = AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.are_sure_you_want_to_exit))
                //                .setMessage(activity.getString(R.string.i_feel_you_might_wanna_rate_me))
                .setPositiveButton(activity.getString(R.string.exit), listener)
                .setNegativeButton(activity.getString(R.string.rate), listener)
                .setNeutralButton(activity.getString(R.string.channel), listener)
                .create()

        dialog.setOnShowListener {
            activity.setFont(dialog.findViewById<View>(android.R.id.message) as TextView?)
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_NEGATIVE))
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_NEUTRAL))
            activity.setFont(dialog.getButton(AlertDialog.BUTTON_POSITIVE))
        }

        dialog.show()

    }

    //todo: check onepf.com for publishing


    fun goToBotInTelegram(activity: BaseActivity) {
        if (activePack != null) {
            //            if (BaseActivity.isTelegramInstalled) {
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_TO_BOT))
            myIntent.setPackage(activePack)
            activity.startActivity(myIntent)
        } else
            Toast.makeText(activity, activity.getString(R.string.telegram_is_not_installed), Toast.LENGTH_SHORT).show()
    }

    fun deviceLanguageIsPersian(): Boolean {
        return Locale.getDefault().language == "fa"
    }

    fun deviceLanguageIsRussian(): Boolean {
        return Locale.getDefault().language == "ru"
    }


    fun convertToPersianNumber(s: String): String {
        val length = s.length
        val temp = StringBuilder()
        for (i in 0 until length) {
            temp.append(Character.toString((s[i].toInt() + 1728).toChar()))
        }
        return temp.toString()
    }

    fun getAllAvailableModes(activity: BaseActivity): ArrayList<Mode> {
        val modes = ArrayList<Mode>()

        var tempMode: Mode
        for (pack in Constants.availableFormats) {
            tempMode = Mode(pack, activity)
            if (tempMode.isAvailable)
                modes.add(tempMode)
        }
        return modes
    }

    fun createTrimmedBitmap(bmp: Bitmap): Bitmap {
        var bmp = bmp

        val imgHeight = bmp.height
        val imgWidth = bmp.width
        val smallX = 0
        val smallY = 0
        var left = imgWidth
        var right = imgWidth
        var top = imgHeight
        var bottom = imgHeight
        for (i in 0 until imgWidth) {
            for (j in 0 until imgHeight) {
                if (bmp.getPixel(i, j) != Color.TRANSPARENT) {
                    if (i - smallX < left) {
                        left = i - smallX
                    }
                    if (imgWidth - i < right) {
                        right = imgWidth - i
                    }
                    if (j - smallY < top) {
                        top = j - smallY
                    }
                    if (imgHeight - j < bottom) {
                        bottom = imgHeight - j
                    }
                }
            }
        }
        Log.d(TAG, "left:$left right:$right top:$top bottom:$bottom")
        bmp = Bitmap.createBitmap(bmp, left, top, imgWidth - left - right, imgHeight - top - bottom)
        return bmp
    }

    fun setLocale(lang: Int, activity: BaseActivity) {
        var language: String? = null
        when (lang) {
            Constants.PERSIAN_LANGUAGE -> language = "fa"
            Constants.ENGLISH_LANGUAGE -> language = "en"
            Constants.RUSSIAN_LANGUAGE -> language = "ru"
            Constants.GERMAN_LANGUAGE -> language = "de"
            Constants.SYSTEM_LANGUAGE -> language = Locale.getDefault().language
        }
        if (language != null) {
            val locale = Locale(language)

            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)

            activity.baseContext.resources.updateConfiguration(config,
                    activity.baseContext.resources.displayMetrics)
        }
    }


    fun getCachedImage(dir: String): Bitmap? {
        return if (File(dir).exists()) {
            //            Log.e(TAG, dir);
            BitmapFactory.decodeFile(dir)
        } else null
        //        Log.e(TAG, "file didn't exist");
        //        return null;
    }

    fun cacheImage(mBitmap: Bitmap, dir: String) {
        try {
            val file = File(dir)
            //            Log.e(TAG, "cacheImage: " + dir);
            if (!file.parentFile!!.exists())
                if (file.parentFile!!.mkdirs())
                    Log.v(TAG, "couldn't make parent directory")
            //                    return false;
            if (!file.exists()) {
                if (!file.createNewFile())
                    Log.v(TAG, "couldn't make parent directory")
            } else {
                file.delete()
                file.createNewFile()
            }

            val outputStream = FileOutputStream(dir)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source com.amir.stickergram.image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    fun convertToMutable(imgIn: Bitmap): Bitmap {
        var imgIn = imgIn
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a com.amir.stickergram.image, it will store the raw com.amir.stickergram.image data.
            val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + "temp.tmp")

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            val randomAccessFile = RandomAccessFile(file, "rw")

            // get the width and height of the source bitmap.
            val width = imgIn.width
            val height = imgIn.height
            val type = imgIn.config

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            val channel = randomAccessFile.channel
            val map = channel.map(FileChannel.MapMode.READ_WRITE, 0, (imgIn.rowBytes * height).toLong())
            imgIn.copyPixelsToBuffer(map)
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle()
            System.gc()// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type)
            map.position(0)
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map)
            //close the temporary file and channel , then delete that also
            channel.close()
            randomAccessFile.close()

            // delete the temp file
            file.delete()

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imgIn
    }

    fun removeDirectory(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                removeDirectory(child)

        fileOrDirectory.delete()
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun createFolderStructure(file: File): Boolean {
        try {
            file.mkdirs()
            if (file.exists())
                file.delete()
            file.createNewFile()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    fun getUserStickerDirectories(context: Context, activity: BaseActivity?): List<String>? {
        val file = File(Constants.USER_STICKERS_DIRECTORY)
        if (!file.exists())
            if (checkPermission(context))
                file.mkdirs()
            else {
                if (activity != null)
                    gainPermission(activity, 0)
                return null
            }
        val files = file.listFiles() ?: return null
        val directories = ArrayList<String>()
        for (file1 in files) {
            if (!file1.isFile) {
                directories.add(file1.absolutePath)
            }
        }
        return directories
    }

}