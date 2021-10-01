package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.core.NotificationKeys
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)
       
        val result: Boolean? = intent?.extras?.getBoolean(NotificationKeys.KEY_RESULT)
        val source: String? = intent?.extras?.getString(NotificationKeys.KEY_SOURCE)

        Log.d("===>>>", result.toString())
        Log.d("===>>>", source.toString())

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
