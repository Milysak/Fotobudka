package com.example.fotobudka.fragmentsVM

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.fotobudka.APIvm
import com.example.fotobudka.Constants
import com.example.fotobudka.dataM.database.AppDatabase
import com.example.fotobudka.dataM.database.Settings
import com.example.fotobudka.R
import com.example.fotobudka.databinding.ActivityMainBinding
import com.example.fotobudka.databinding.FragmentMakingPhotosBinding
import com.example.fotobudka.mInterface
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MakingPhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MakingPhotosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var appDatabase: AppDatabase

    private var _binding: FragmentMakingPhotosBinding? = null
    private val binding get() = _binding!!

    private var _bindingMain: ActivityMainBinding? = null
    //private val bindingMain get() = _bindingMain!!

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    //private lateinit var settings: Settings

    private lateinit var restAPIvm: APIvm

    private var posBanner = 0
    private var posFilter = 0
    private var numberOfPhotos = 0
    private var intervalNumber = 0

    private val audio = ToneGenerator(AudioManager.STREAM_MUSIC, 1000)

    //private var base64MultiplePhotos: MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMakingPhotosBinding.inflate(inflater, container, false)
        _bindingMain = ActivityMainBinding.inflate(inflater, container, false)

        appDatabase = AppDatabase.getDatabase(requireContext())

        runBlocking(Dispatchers.IO) {
            appDatabase.settingsDao().deleteAll()
            val settings = Settings(6, 5, 0, 0)
            appDatabase.settingsDao().insert(settings)
        }

        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        GlobalScope.launch(Dispatchers.IO) {
            var actualBanner = ""
            var actualFilter = ""
            posBanner = appDatabase.settingsDao().getActualBanner()
            posFilter = appDatabase.settingsDao().getActualFilter()
            numberOfPhotos = appDatabase.settingsDao().getPhotosNumber()
            intervalNumber = appDatabase.settingsDao().getIntervalBetween()

            actualBanner = if(posBanner == 0) {
                "Kolorowy"
            } else if(posBanner == 1) {
                "Kosmos"
            } else if(posBanner == 2) {
                "Sylwester"
            } else {
                "Święta"
            }

            actualFilter = if(posFilter == 0) {
                "Oryginalny"
            } else if(posFilter == 1) {
                "Czarno - Biały"
            } else {
                "Ulepszony"
            }

            binding.settingsOnCamera.text = "Zdjęć: ${appDatabase.settingsDao().getPhotosNumber()} Interwał: ${appDatabase.settingsDao().getIntervalBetween()}\n" +
                    "Baner: $actualBanner Filtr: $actualFilter"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restAPIvm = APIvm("192.168.1.102", "5000")

        outputDirectory = getOutputDirectory()

        runBlocking(Dispatchers.IO) {
            var actualBanner = ""
            var actualFilter = ""
            posBanner = appDatabase.settingsDao().getActualBanner()
            posFilter = appDatabase.settingsDao().getActualFilter()
            numberOfPhotos = appDatabase.settingsDao().getPhotosNumber()
            intervalNumber = appDatabase.settingsDao().getIntervalBetween()

            actualBanner = if(posBanner == 0) {
                "Kolorowy"
            } else if(posBanner == 1) {
                "Kosmos"
            } else if(posBanner == 2) {
                "Sylwester"
            } else {
                "Święta"
            }

            actualFilter = if(posFilter == 0) {
                "Oryginalny"
            } else if(posFilter == 1) {
                "Czarno - Biały"
            } else {
                "Ulepszony"
            }

            binding.settingsOnCamera.text = "Zdjęć: ${appDatabase.settingsDao().getPhotosNumber().toString()} Interwał: ${appDatabase.settingsDao().getIntervalBetween()}\n" +
                    "Baner: $actualBanner Filtr: $actualFilter"
        }

        if(allPermissionGranted()){
            startCamera()
        } else {
            Toast.makeText(activity?.applicationContext,"Permissions requested", Toast.LENGTH_SHORT).show()
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,arrayOf(Manifest.permission.CAMERA),123)
            }
        }

        val buttonStart = view.findViewById<CardView>(R.id.button_start)
        buttonStart.setOnClickListener {

            seriesOfPhotos()

            //restAPIvm.createPDF(posBanner, posFilter, numberOfPhotos)

            //if(restAPIvm.sendPhotosToServerAndThenConvertToPDF(posBanner, posFilter, numberOfPhotos)) Toast.makeText(binding.root.context, "Request wysłany!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOutputDirectory(): File{
        val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let{ mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply{
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity?.filesDir!!
    }

    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    private fun seriesOfPhotos() {
        GlobalScope.launch(Dispatchers.Main) {
            val listener = activity as mInterface?

            binding.settingsPlace.isVisible = false
            binding.buttonStart.isVisible = false

            binding.root.isClickable = false

            listener?.update(false)

            binding.textCounter.text = "Start!"
            delay(1000)

                for (x in 1..numberOfPhotos) {
                    binding.numberOfMadePhotos.text = "${x}/$numberOfPhotos"

                    for (y in 1..intervalNumber) {
                        if (intervalNumber - y in 0..2) {
                            audio.startTone(ToneGenerator.TONE_SUP_PIP, 150)
                        }

                        binding.textCounter.text = (intervalNumber - y + 1).toString()

                        if (intervalNumber - y == 0) {
                            binding.frameFlash.strokeWidth = 7
                        }

                        delay(1000)
                    }

                    binding.textCounter.text = ""
                    binding.frameFlash.strokeWidth = 15

                    audio.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 500)

                    takePhoto()
                    delay(500)
                    binding.frameFlash.strokeWidth = 0
                }

            binding.textCounter.text = "Koniec!"
            audio.startTone(ToneGenerator.TONE_CDMA_ABBR_INTERCEPT, 750)
            delay(1000)
            binding.textCounter.text = ""

            listener?.update(true)

            binding.root.isClickable = true

            binding.buttonStart.isVisible = true
            binding.settingsPlace.isVisible = true

            binding.numberOfMadePhotos.text = ""
        }

    }

    private fun takePhoto(): String{
        val imageCapture = imageCapture?: return ""
        val name = SimpleDateFormat(Constants.FILE_NAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
        val photoFile = File(outputDirectory, name)
        var photoPath = ""

        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOption, ContextCompat.getMainExecutor(requireContext()),
            object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    photoPath = savedUri.path.toString()

                    //val stringImage64 = restAPIvm.convertPhotoToBase64(photoPath)
                    //Toast.makeText(requireActivity(), stringImage64, Toast.LENGTH_LONG).show()
                    restAPIvm.sendImageToServer(photoFile.path, posFilter, posBanner, numberOfPhotos)

                }
                override fun onError(exception: ImageCaptureException) {
                    Log.d(Constants.TAG, "onError: ${exception.message}",exception)
                }
            }
        )
        return photoPath
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { mPreview->
                mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }catch (e:Exception){
                Log.d(Constants.TAG, "startCamera Fail:", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                requireActivity().baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if(requestCode == Constants.REQUEST_CODE_PERMISSIONS){
            if(allPermissionGranted()){
                startCamera()
            }else{
                Toast.makeText(requireContext(),"Permissions not granted by the user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MakingPhotosFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MakingPhotosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}