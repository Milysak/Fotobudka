package com.example.fotobudka

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView

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
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        val colorfullBanner = view.findViewById<MaterialCardView>(R.id.banner1)
        val darknessBanner = view.findViewById<MaterialCardView>(R.id.banner2)
        val silvesterBanner = view.findViewById<MaterialCardView>(R.id.banner3)
        val christmasBanner = view.findViewById<MaterialCardView>(R.id.banner4)

        colorfullBanner.strokeWidth = strokeWidth

        colorfullBanner.setOnClickListener {
            colorfullBanner.strokeWidth = strokeWidth
            darknessBanner.strokeWidth = 0
            silvesterBanner.strokeWidth = 0
            christmasBanner.strokeWidth = 0
        }
        darknessBanner.setOnClickListener {
            colorfullBanner.strokeWidth = 0
            darknessBanner.strokeWidth = strokeWidth
            silvesterBanner.strokeWidth = 0
            christmasBanner.strokeWidth = 0
        }
        silvesterBanner.setOnClickListener {
            colorfullBanner.strokeWidth = 0
            darknessBanner.strokeWidth = 0
            silvesterBanner.strokeWidth = strokeWidth
            christmasBanner.strokeWidth = 0
        }
        christmasBanner.setOnClickListener {
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
            originalFilter.strokeWidth = strokeWidth
            blackandwhiteFilter.strokeWidth = 0
            boostFilter.strokeWidth = 0
        }
        blackandwhiteFilter.setOnClickListener {
            originalFilter.strokeWidth = 0
            blackandwhiteFilter.strokeWidth = strokeWidth
            boostFilter.strokeWidth = 0
        }
        boostFilter.setOnClickListener {
            originalFilter.strokeWidth = 0
            blackandwhiteFilter.strokeWidth = 0
            boostFilter.strokeWidth = strokeWidth
        }

        return view
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