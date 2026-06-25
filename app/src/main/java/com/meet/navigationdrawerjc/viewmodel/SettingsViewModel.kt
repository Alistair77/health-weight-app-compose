package com.meet.navigationdrawerjc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meet.navigationdrawerjc.data.PreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferencesDataStore: PreferencesDataStore) : ViewModel() {

    // Expose the motivation setting as a StateFlow
    val motivation: StateFlow<Int> = preferencesDataStore.motivation
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Expose the ability setting as a StateFlow
    val ability: StateFlow<Int> = preferencesDataStore.ability
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Expose the triggers setting as a StateFlow
    val triggers: StateFlow<Boolean> = preferencesDataStore.triggers
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Function to update the motivation setting
    fun setMotivation(value: Int) {
        viewModelScope.launch {
            preferencesDataStore.updateMotivation(value)
        }
    }

    // Function to update the ability setting
    fun setAbility(value: Int) {
        viewModelScope.launch {
            preferencesDataStore.updateAbility(value)
        }
    }

    // Function to update the triggers setting
    fun setTriggers(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.updateTriggers(enabled)
        }
    }

    // Function to update the motivation setting
    fun updateMotivation(value: Int) {
        viewModelScope.launch {
            preferencesDataStore.updateMotivation(value)
        }
    }

    // Function to update the ability setting
    fun updateAbility(value: Int) {
        viewModelScope.launch {
            preferencesDataStore.updateAbility(value)
        }
    }

    // Function to update the triggers setting
    fun updateTriggers(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.updateTriggers(enabled)
        }
    }
}
