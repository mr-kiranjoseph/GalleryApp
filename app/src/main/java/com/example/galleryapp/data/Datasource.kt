package com.example.galleryapp.data

import com.example.galleryapp.R
import com.example.galleryapp.model.GalleryImage

class Datasource {

    fun loadGalleryImages(): List<GalleryImage>{
        return (listOf(GalleryImage(R.drawable.image1),
            GalleryImage(R.drawable.image2),
            GalleryImage(R.drawable.image3),
            GalleryImage(R.drawable.image4),
            GalleryImage(R.drawable.image5),
            GalleryImage(R.drawable.image6),
            GalleryImage(R.drawable.image7),
            GalleryImage(R.drawable.image8),
            GalleryImage(R.drawable.image9)))
    }
}