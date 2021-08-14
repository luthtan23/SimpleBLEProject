package com.luthtan.samplebleproject.feature_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.luthtan.simplebleproject.data.repository.PreferencesRepository
import org.koin.android.ext.android.inject

class SampleFeatureActivity : AppCompatActivity() {

    private val preference: PreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_feature)

    }
}