package com.sportpedia.ui.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sportpedia.R
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.AnkoLogger

class ProfileFragment : Fragment(), AnkoLogger {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentText.text = this::class.java.simpleName
    }
}