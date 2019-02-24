package com.sportpedia.ui.booking.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sportpedia.R
import com.sportpedia.adapter.VenueAdapter
import com.sportpedia.model.Venue
import com.sportpedia.ui.booking.viewmodel.VenueViewModel
import com.sportpedia.util.Const
import com.sportpedia.util.TimeUtil
import kotlinx.android.synthetic.main.fragment_venue.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class VenueFragment : Fragment(), AnkoLogger {
    private lateinit var viewModel: VenueViewModel
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var venueAdp: VenueAdapter
    private val venues = mutableListOf<Venue>()

    private val spListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            viewModel.category.value = spCategory.selectedItem as String
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(VenueViewModel::class.java)
        }

        venueAdp = VenueAdapter(context!!, venues) {
            info { "bookedId: ${TimeUtil.toBookedId(tvDate.text.toString())}" }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_venue, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvShimmer.showShimmerAdapter()
        rvContent.layoutManager = LinearLayoutManager(context!!)
        rvContent.adapter = venueAdp
        spCategory.onItemSelectedListener = spListener
        tvDate.text = TimeUtil.today()
        tvDate.setOnClickListener {
            TimeUtil.setDate(context!!, it)
        }

        viewModel.category.observe(this, Observer {
            info { "category: $it" }
            getVenueByCategory(it)
        })
    }

    private fun getVenueByCategory(category: String) {
        firestore.collection(Const.venue)
            .whereEqualTo(Const.category, category.toLowerCase())
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    info { "doc size: ${it.result!!.documents.size}" }
                    venues.clear()
                    for (doc in it.result!!.documents) {
                        val venue = doc.toObject(Venue::class.java)!!
                        venue.id = doc.id
                        venues.add(venue)
                    }

                    venueAdp.notifyDataSetChanged()
                    rvShimmer.hideShimmerAdapter()
                    rvContent.visibility = View.VISIBLE
                }
            }
    }
}