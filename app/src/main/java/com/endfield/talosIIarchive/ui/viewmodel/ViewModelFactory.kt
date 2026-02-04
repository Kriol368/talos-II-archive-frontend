package com.endfield.talosIIarchive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepository


class OperatorViewModelFactory(private val repository: OperatorRepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OperatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OperatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WeaponViewModelFactory(
    private val repository: WeaponRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeaponViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeaponViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}