package com.yogigupta1206.btcethscanner.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.yogigupta1206.btcethscanner.R
import com.yogigupta1206.btcethscanner.databinding.FragmentQrCodeBinding
import com.yogigupta1206.utils.ETHEREUM
import com.yogigupta1206.utils.QR_SCANNED_RESULT
import com.yogigupta1206.utils.QR_SCANNED_TYPE
import com.yogigupta1206.utils.validator

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
        mBinding.toolbar.title.text = "QR Result"
        resources.getString(R.string.qr_result, qrResult)
        setClickListeners()
    }

    private fun setClickListeners() {
        mBinding.toolbar.imgBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        mBinding.btnValidate.setOnClickListener {
            if(validator(qrType, qrResult)){
                Toast.makeText(requireContext(), "Valid Address", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Invalid Address", Toast.LENGTH_SHORT).show()
            }
        }
        mBinding.btnShare.setOnClickListener {
            if(validator(qrType, qrResult)){
                shareAddress()
            }else{
                Toast.makeText(requireContext(), "Invalid Address", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareAddress() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, QR_SCANNED_TYPE)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


}