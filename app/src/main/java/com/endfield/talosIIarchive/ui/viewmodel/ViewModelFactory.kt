package com.endfield.talosIIarchive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.endfield.talosIIarchive.domain.repositoty.BlueprintRepository
import com.endfield.talosIIarchive.domain.repositoty.GearRepository
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepositoryImpl
import com.endfield.talosIIarchive.domain.repositoty.TeamRepository
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepository

//replicar esto para todos los vm
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

class GearViewModelFactory(
    private val repository: GearRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GearViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GearViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class BlueprintViewModelFactory(
    private val repository: BlueprintRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlueprintViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlueprintViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TeamViewModelFactory(
    private val repository: TeamRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}