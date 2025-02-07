package com.example.uberdriver.presentation.auth.inputlayoutadapter

import com.google.android.material.textfield.TextInputLayout
import com.mobsandgeeks.saripaar.adapter.ViewDataAdapter

class TextInputLayoutAdapter : ViewDataAdapter<TextInputLayout, String> {
    override fun getData(view: TextInputLayout): String {
        return view.editText?.text?.toString() ?: ""
    }
}