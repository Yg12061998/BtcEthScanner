package com.yogigupta1206.utils

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

fun FragmentActivity.addReplaceFragment(
    @IdRes container: Int,
    fragment: Fragment,
    addFragment: Boolean,
    addToBackStack: Boolean
) {
    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    if (addFragment) {
        transaction.add(container, fragment, fragment.javaClass.simpleName)
    } else {
        transaction.replace(container, fragment, fragment.javaClass.simpleName)
    }
    if (addToBackStack) {
        transaction.addToBackStack(fragment.tag)
    }
    hideKeyboard()
    transaction.commit()
}

//hide the keyboard
fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun validator(type: String, address:String): Boolean {
    if(type.contentEquals(BITCOIN)){
        if(address.length in 25..34){
            if(address[0].equals('1', true)){
                val list = listOf<Char>('0','O','l','I')
                val testString = address.filter { it !in list }
                if(testString.length == address.length){
                    return true
                }
            }
        }
    }else{
        if(address.startsWith("0x")){
            val testString = address.substring(2).filter { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F'}
            Log.d("address", "\n$testString\n$address")
            if(testString.length == address.length-2){
                return true
            }
        }
    }
    return false
}