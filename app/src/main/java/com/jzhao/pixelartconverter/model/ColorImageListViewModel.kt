package com.jzhao.pixelartconverter.model

import android.graphics.Color
import androidx.lifecycle.ViewModel

class ColorImageListViewModel(): ViewModel() {
    public var colorList = MutableList(5) {(Color())}
}