package com.yogigupta1206.btcethscanner.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yogigupta1206.btcethscanner.R
import com.yogigupta1206.btcethscanner.databinding.FragmentQrCodeBinding
import com.yogigupta1206.utils.ETHEREUM
import com.yogigupta1206.utils.QR_SCANNED_RESULT
import com.yogigupta1206.utils.QR_SCANNED_TYPE

class QRCodeFragment : Fragment() {

    private var qrType = ETHEREUM
    private var qrResult = ""
    private lateinit var mBinding: FragmentQrCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            qrType = it.getString(QR_SCANNED_TYPE).toString()
            qrResult = it.getString(QR_SCANNED_RESULT).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentQrCodeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}