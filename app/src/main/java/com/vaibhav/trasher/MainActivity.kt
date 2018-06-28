package com.vaibhav.trasher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.vaibhav.trasher.classifier.*
import com.vaibhav.trasher.classifier.tensorflow.ImageClassifierFactory
import com.vaibhav.trasher.utils.getCroppedBitmap
import com.vaibhav.trasher.utils.getUriFromFilePath
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

private const val REQUEST_PERMISSIONS = 1
private const val REQUEST_TAKE_PICTURE = 2

class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private lateinit var classifier: Classifier
    private var photoFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
    }

    private fun checkPermissions() {
        if (arePermissionsAlreadyGranted()) {
            init()
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionsAlreadyGranted() =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS && arePermissionGranted(grantResults)) {
            init()
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionGranted(grantResults: IntArray) =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    private fun init() {
        createClassifier()
        takePhoto()
    }

    private fun createClassifier() {
        classifier = ImageClassifierFactory.create(
                assets,
                GRAPH_FILE_PATH,
                LABELS_FILE_PATH,
                IMAGE_SIZE,
                GRAPH_INPUT_NAME,
                GRAPH_OUTPUT_NAME
        )
    }

    private fun takePhoto() {
        photoFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/${System.currentTimeMillis()}.jpg"
        val currentPhotoUri = getUriFromFilePath(this, photoFilePath)

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.take_photo) {
            takePhoto()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val file = File(photoFilePath)
        if (requestCode == REQUEST_TAKE_PICTURE && file.exists()) {
            classifyPhoto(file)
        }
    }

    private fun classifyPhoto(file: File) {
        val photoBitmap = BitmapFactory.decodeFile(file.absolutePath)
        val croppedBitmap = getCroppedBitmap(photoBitmap)
        classifyAndShowResult(croppedBitmap)
        imagePhoto.setImageBitmap(photoBitmap)
    }

    private fun classifyAndShowResult(croppedBitmap: Bitmap) {
        runInBackground(
                Runnable {
                    val result = classifier.recognizeImage(croppedBitmap)
                    showResult(result)
                })
    }

    @Synchronized
    private fun runInBackground(runnable: Runnable) {
        handler.post(runnable)
    }

    private fun showResult(result: Result) {

        textResult.text = trash(result);
        layoutContainer.setBackgroundColor(getColorFromResult(result.result))
    }

    @Suppress("DEPRECATION")
    private fun getColorFromResult(result: String): Int {
        return if (result == getString(R.string.hot)) {
            resources.getColor(R.color.hot)
        } else {
            resources.getColor(R.color.not)
        }
    }
    private fun trash(result : Result): String {
        var display = "";
        var r = result.result;
        var conf = result.confidence;
        if(r.equals("aluminium foil")){
            if(conf.compareTo(0.300000)>0){
                display = "RECYCLABLE";
            }else{
                display = "TRASH";
                conf = 1-result.confidence;
            }
        }
        else if(r.equals("batteries")){
            if(conf.compareTo(0.3555555)>0){
                display = "E-WASTE";
            }else{
                display = "TRASH";
                conf = 1-result.confidence;
            }
        }
        else if(r.equals("cans")){
            if(conf.compareTo(0.3789887)>0){
                display = "RECYCLABLE";
            }else{
                display = "TRASH";
                conf = 1-result.confidence;
            }
        }
        else if(r.equals("cardboard")){
            if(conf.compareTo(0.35555555)>0){
                display = "RECYCLABLE";
            }else{
                display = "TRASH";
                conf = 1-result.confidence;
            }
        }
        else if(r.equals("chips packet")){
            display = "TRASH";
            if(conf.compareTo(0.6550000)>0)
            { conf = (0-conf)*-1;}

        }
        else if(r.equals("egg shells")){
            if(conf.compareTo(0.55555555)>0){
                display = "COMPOSTABLE ORGANIC WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("food waste")){
            if(conf.compareTo(0.38888888)>0){
                display = "COMPOSTABLE ORGANIC WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("fridge")){
            if(conf.compareTo(0.450000)>0){
                display = "E-WASTE";
            }else if(conf.compareTo(0.2555555)>0) {
                display = "RECYCLABLE";
                conf = 1 - conf;
            }
            else{
                display = "TRASH";
                conf = 1 - conf;
            }
        }

        else if(r.equals("glass")){
            if(conf.compareTo(0.400500)>0){
                display = "RECYCLABLE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("keyboard")){
            if(conf.compareTo(0.38555555)>0){
                display = "E-WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("metals")){
            if(conf.compareTo(0.39555555)>0){
                display = "RECYCLABLE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("mobile phone")){
            if(conf.compareTo(0.5000000)>0){
                display = "E-WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("mouse")){
            if(conf.compareTo(0.5000000)>0){
                display = "E-WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("paper")){
            if(conf.compareTo(0.400000)>0){
                display = "RECYCLABLE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("phone battery")){
            if(conf.compareTo(0.6000000)>0){
                display = "E-WASTE"
            }else{
                display = "TRASH";
                conf = (0-conf)*-1;
            }
        }
        else if(r.equals("plastic")){
            if(conf.compareTo(0.4000000)>0){
                display = "RECYClABLE"
            }else {
                display = "TRASH";
                conf = 1 - conf;
            }
        }
        else if(r.equals("polythene bag")){
            if(conf.compareTo(0.5555555)>0){
                display = "TRASH"
            }else {
                display = "RECYCLABLE";
                conf = 1 - conf;
            }
        }
        else if(r.equals("styrofoam")){
            if(conf.compareTo(0.6000000)>0){
                display = "TRASH"
            }else {
                display = "RECYCLABLE";
                conf = 1 - conf;
            }
        }
        else if(r.equals("syringes")){
            if(conf.compareTo(0.7000000)>0){
                display = "MEDICAL WASTE : DISPOSE RESPONSIBLY"
            }else {
                display = "TRASH";
                conf = 1 - conf;
            }
        }
        else if(r.equals("tetra pack")){
            if(conf.compareTo(0.4555555)>0){
                display = "RECYCLABLE"
            }else {
                display = "TRASH";
                conf = 1 - conf;
            }
        }
        else if(r.equals("tires")){
            if(conf.compareTo(0.52775555)>0){
                display = "RECYCLABLE"
            }else {
                display = "TRASH";
                conf = 1 - conf;
            }
        }
        else if(r.equals("wood")){
            if(conf.compareTo(0.50000000)>0){
                display = "RECYCLABLE"
            }else {
                display = "TRASH";
                conf = 1 - conf;
            }
        }
        return display + "\n Confidence: "+ ((conf*100).toString()).substring(0,4)+ "%";

    }
}
