/*
 * Copyright 2021 headuck (https://blog.headuck.com/)
 *
 * This file is part of GoOutWithDuck
 *
 * GoOutWithDuck is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GoOutWithDuck is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GoOutWithDuck. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bus.app.backhomeduck5duck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bus.app.backhomeduck5duck.databinding.FragmentScanDoneBinding
import com.bus.app.backhomeduck5duck.utilities.navigateUpSafe
import com.bus.app.backhomeduck5duck.utilities.observeOnce
import com.bus.app.backhomeduck5duck.utilities.setBackPressHandler
import com.bus.app.backhomeduck5duck.viewmodels.BottomNavSharedViewModel
import com.bus.app.backhomeduck5duck.viewmodels.ScanDoneViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class ScanDoneFragment : Fragment() {

    private val viewModel: ScanDoneViewModel by viewModels()
    private val bottomNavSharedViewModel: BottomNavSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), defaultViewModelProviderFactory).get(BottomNavSharedViewModel::class.java)
    }
    private val venueVisitInfoArgs: ScanDoneFragmentArgs by navArgs()
    private lateinit var binding: FragmentScanDoneBinding;


    interface Callback {
        fun onDismiss(view: View)
        fun onLeaveButtonClick(view: View)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentScanDoneBinding.inflate(inflater, container, false)
        setBackPressHandler()
        bottomNavSharedViewModel.setBottomNavHidden(true)

        binding.venueVisitInfo = venueVisitInfoArgs.venueVisitInfo

        binding.callback = object: Callback {
            override fun onDismiss(view: View) {
                this@ScanDoneFragment.navigateUpSafe()
            }

            override fun onLeaveButtonClick(view: View) {
                Timber.d("Leave requested")
//                viewModel.leaveVenueNow(venueVisitInfoArgs.venueVisitInfo.id).observe(viewLifecycleOwner){ result ->
//                    Timber.d("Leave result received")
//                    result.onFailure {
//                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG)
//                                .setDuration(SNACK_DURATION)
//                                .addCallback(
//                                        object : Snackbar.Callback() {
//                                            override fun onDismissed(snackbar: Snackbar, event: Int) {
//                                                super.onDismissed(snackbar, event)
//                                                this@ScanDoneFragment.navigateUpSafe()
//                                            }
//                                        }
//                                )
//                                .show()
//                    }.onSuccess {
//                        this@ScanDoneFragment.navigateUpSafe()
//                    }
//
//                }
                this@ScanDoneFragment.navigateUpSafe()
            }
        }

        // Spinner
        val durationArray = arrayOf<String?>(*resources.getStringArray(R.array.scan_done_auto_leave_duration))
        context?.let{
            val arrayAdapter = ArrayAdapter<String?>(it, R.layout.spinner_scan_done, durationArray)
            binding.scanDoneAutoLeaveSpinner.adapter = arrayAdapter
        }

        val spinnerListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!binding.scanDoneCheckBox.isChecked) {
                    binding.scanDoneCheckBox.isChecked = true
                }
                viewModel.setAutoCheckout(venueVisitInfoArgs.venueVisitInfo.id, true, position, true)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        if (savedInstanceState == null) {
            // Read from preference
            viewModel.autoCheckoutSetting.observeOnce(viewLifecycleOwner) {
                binding.scanDoneAutoLeaveSpinner.setSelection(it.second, true)
                binding.scanDoneAutoLeaveSpinner.onItemSelectedListener = spinnerListener
                binding.scanDoneCheckBox.isChecked = it.first
                // save to visit info record only
                viewModel.setAutoCheckout(venueVisitInfoArgs.venueVisitInfo.id, it.first,
                        it.second, false)
            }
        } else {
            // Just set listener
            binding.scanDoneAutoLeaveSpinner.onItemSelectedListener = spinnerListener
        }

        // Checkbox
        binding.scanDoneCheckBox.setOnClickListener {
            viewModel.setAutoCheckout(venueVisitInfoArgs.venueVisitInfo.id, binding.scanDoneCheckBox.isChecked,
                    binding.scanDoneAutoLeaveSpinner.selectedItemPosition, true)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.callback = null
    }
}