package com.example.fotobudka.fragmentsVM

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fotobudka.dataM.database.AppDatabase
import com.example.fotobudka.dataM.database.Settings
import com.example.fotobudka.R
import com.example.fotobudka.databinding.FragmentMainMenuBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenu : Fragment() {

    private val strokeWidth: Int = 5

    private var numberOfPhotos by Delegates.notNull<Int>()
    private var intervalBetweenPhotos by Delegates.notNull<Int>()
    private var actualBanner by Delegates.notNull<Int>()
    private var actualFilter by Delegates.notNull<Int>()

    private lateinit var appDatabase: AppDatabase
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        appDatabase = AppDatabase.getDatabase(requireContext())

        runBlocking(Dispatchers.IO) {
            numberOfPhotos = appDatabase.settingsDao().getPhotosNumber()
            intervalBetweenPhotos = appDatabase.settingsDao().getIntervalBetween()
            actualBanner = appDatabase.settingsDao().getActualBanner()
            actualFilter = appDatabase.settingsDao().getActualFilter()
        }

        numberOfPhotos = binding.seekBarNumberOfPhotos.value.toInt()
        intervalBetweenPhotos = binding.seekBarInterval.value.toInt()

        return binding.root
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sliderNumberOfPhotos = view.findViewById<Slider>(R.id.seekBarNumberOfPhotos)
        sliderNumberOfPhotos.addOnChangeListener { slider, value, fromUser ->
            numberOfPhotos = value.toInt()
            updateDatabase()
        }

        val sliderIntervalBetweenPhotos = view.findViewById<Slider>(R.id.seekBarInterval)
        sliderIntervalBetweenPhotos.addOnChangeListener { slider, value, fromUser ->
            intervalBetweenPhotos = value.toInt()
            updateDatabase()
        }

        val colorfullBanner = view.findViewById<MaterialCardView>(R.id.banner1)
        val darknessBanner = view.findViewById<MaterialCardView>(R.id.banner2)
        val silvesterBanner = view.findViewById<MaterialCardView>(R.id.banner3)
        val christmasBanner = view.findViewById<MaterialCardView>(R.id.banner4)

        colorfullBanner.strokeWidth = strokeWidth

        colorfullBanner.setOnClickListener {
            actualBanner = 0
            updateDatabase()

            colorfullBanner.strokeWidth = strokeWidth
            darknessBanner.strokeWidth = 0
            silvesterBanner.strokeWidth = 0
            christmasBanner.strokeWidth = 0
        }
        darknessBanner.setOnClickListener {
            actualBanner = 1
            updateDatabase()

            colorfullBanner.strokeWidth = 0
            darknessBanner.strokeWidth = strokeWidth
            silvesterBanner.strokeWidth = 0
            christmasBanner.strokeWidth = 0
        }
        silvesterBanner.setOnClickListener {
            actualBanner = 2
            updateDatabase()

            colorfullBanner.strokeWidth = 0
            darknessBanner.strokeWidth = 0
            silvesterBanner.strokeWidth = strokeWidth
            christmasBanner.strokeWidth = 0
        }
        christmasBanner.setOnClickListener {
            actualBanner = 3
            updateDatabase()

            colorfullBanner.strokeWidth = 0
            darknessBanner.strokeWidth = 0
            silvesterBanner.strokeWidth = 0
            christmasBanner.strokeWidth = strokeWidth
        }

        val originalFilter = view.findViewById<MaterialCardView>(R.id.filtr1)
        val blackandwhiteFilter = view.findViewById<MaterialCardView>(R.id.filtr2)
        val boostFilter = view.findViewById<MaterialCardView>(R.id.filtr3)

        originalFilter.strokeWidth = strokeWidth;

        originalFilter.setOnClickListener {
            actualFilter = 0
            updateDatabase()

            originalFilter.strokeWidth = strokeWidth
            blackandwhiteFilter.strokeWidth = 0
            boostFilter.strokeWidth = 0
        }
        blackandwhiteFilter.setOnClickListener {
            actualFilter = 1
            updateDatabase()

            originalFilter.strokeWidth = 0
            blackandwhiteFilter.strokeWidth = strokeWidth
            boostFilter.strokeWidth = 0
        }
        boostFilter.setOnClickListener {
            actualFilter = 2
            updateDatabase()

            originalFilter.strokeWidth = 0
            blackandwhiteFilter.strokeWidth = 0
            boostFilter.strokeWidth = strokeWidth
        }
    }

    private fun updateDatabase() {
        runBlocking(Dispatchers.IO) {
            appDatabase.settingsDao().deleteAll()
            val settings = Settings(numberOfPhotos, intervalBetweenPhotos, actualBanner, actualFilter)
            appDatabase.settingsDao().insert(settings)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}