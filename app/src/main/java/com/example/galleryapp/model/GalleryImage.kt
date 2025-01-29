package com.example.galleryapp.model

import androidx.annotation.DrawableRes
import org.intellij.lang.annotations.Identifier


data class GalleryImage(

   @DrawableRes val imageResourceID: Int,
   @Identifier val uniqueImageID: Int = imageResourceID
)
