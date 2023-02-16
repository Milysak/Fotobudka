package com.example.fotobudka

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.fotobudka.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity(), mInterface {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        viewPager = findViewById(R.id.viewPager)

        viewPager.adapter = ViewPagerAdapter(this)
    }

    override fun onBackPressed() {
        // Do nothing...
    }

    // To block swiping between a fragments when taking photos
    override fun update(state: Boolean) {
        viewPager.isUserInputEnabled = state
    }
}

interface mInterface {
    fun update(state: Boolean)
}