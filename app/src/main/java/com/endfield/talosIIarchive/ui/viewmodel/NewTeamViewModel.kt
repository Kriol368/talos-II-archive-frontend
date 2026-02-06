package com.endfield.talosIIarchive.ui.screens.social

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endfield.talosIIarchive.domain.models.*
import com.endfield.talosIIarchive.domain.repositoty.OperatorRepository
import com.endfield.talosIIarchive.domain.repositoty.WeaponRepository
import com.endfield.talosIIarchive.domain.repositoty.GearRepository
import com.endfield.talosIIarchive.ui.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

enum class GearType(val dbValue: String) {
    ARMOR("Armor"),
    GLOVES("Gloves"),
    KIT1("Kit"),
    KIT2("Kit")
}

sealed class SelectionItem {
    data class OperatorItem(val operator: Operator) : SelectionItem()
    data class WeaponItem(val weapon: Weapon) : SelectionItem()
    data class GearItem(val gear: Gear) : SelectionItem()
}

class NewTeamViewModel(
    private val operatorRepository: OperatorRepository,
    private val weaponRepository: WeaponRepository,
    private val gearRepository: GearRepository
) : ViewModel() {

    // Form data
    var teamName by mutableStateOf("")
    var description by mutableStateOf("")

    // Selections
    var selectedOperators by mutableStateOf<Array<Operator?>>(arrayOf(null, null, null, null))
    var selectedWeapons by mutableStateOf<Array<Weapon?>>(arrayOf(null, null, null, null))
    var selectedGear by mutableStateOf<Map<Int, Map<GearType, Gear?>>>(
        mapOf(
            0 to emptyMap(),
            1 to emptyMap(),
            2 to emptyMap(),
            3 to emptyMap()
        )
    )

    // Available data
    var allOperators by mutableStateOf<List<Operator>>(emptyList())
    var allWeapons by mutableStateOf<List<Weapon>>(emptyList())
    var allGear by mutableStateOf<List<Gear>>(emptyList())

    // UI state
    var isLoading by mutableStateOf(false)
    var isCreating by mutableStateOf(false)
    var showingOperatorDialogFor by mutableStateOf<Int?>(null)
    var showingWeaponDialogFor by mutableStateOf<Int?>(null)
    var showingGearDialogFor by mutableStateOf<Pair<Int, GearType>?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadAllData() {
        viewModelScope.launch {
            isLoading = true
            try {
                allOperators = operatorRepository.getAllOperators()
                allWeapons = weaponRepository.getAllWeapons()
                allGear = gearRepository.getAllGear()
            } catch (e: Exception) {
                errorMessage = "Failed to load data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun availableOperatorsForSelection(): List<SelectionItem.OperatorItem> {
        val selectedIds = selectedOperators.filterNotNull().map { it.id }
        return allOperators
            .filter { it.id !in selectedIds }
            .map { SelectionItem.OperatorItem(it) }
    }

    fun availableWeaponsForSelection(operatorIndex: Int): List<SelectionItem.WeaponItem> {
        val operator = selectedOperators[operatorIndex]
        return if (operator != null) {
            allWeapons
                .filter { it.weaponType == operator.weaponType }
                .map { SelectionItem.WeaponItem(it) }
        } else {
            emptyList()
        }
    }

    fun availableGearForSelection(gearType: GearType): List<SelectionItem.GearItem> {
        return allGear
            .filter { it.gearType.equals(gearType.dbValue, ignoreCase = true) }
            .map { SelectionItem.GearItem(it) }
    }

    fun selectOperator(index: Int, operator: Operator) {
        val newArray = selectedOperators.copyOf()
        newArray[index] = operator
        selectedOperators = newArray

        // Clear weapon for this position if operator changed
        if (selectedWeapons[index] != null) {
            val newWeapons = selectedWeapons.copyOf()
            newWeapons[index] = null
            selectedWeapons = newWeapons
        }

        // Clear gear for this operator
        selectedGear = selectedGear.toMutableMap().apply {
            this[index] = emptyMap()
        }
    }

    fun selectWeapon(index: Int, weapon: Weapon) {
        val newArray = selectedWeapons.copyOf()
        newArray[index] = weapon
        selectedWeapons = newArray
    }

    fun selectGear(operatorIndex: Int, gearType: GearType, gear: Gear) {
        val currentGear = selectedGear[operatorIndex]?.toMutableMap() ?: mutableMapOf()
        currentGear[gearType] = gear
        selectedGear = selectedGear.toMutableMap().apply {
            this[operatorIndex] = currentGear
        }
    }

    fun isFormValid(): Boolean {
        if (teamName.isBlank() || description.isBlank()) return false

        // Check all operators are selected
        if (selectedOperators.any { it == null }) return false

        // Check all weapons are selected
        if (selectedWeapons.any { it == null }) return false

        // Check all gear is selected for each operator
        for (i in 0..3) {
            val operatorGear = selectedGear[i]
            if (operatorGear == null ||
                operatorGear[GearType.ARMOR] == null ||
                operatorGear[GearType.GLOVES] == null ||
                operatorGear[GearType.KIT1] == null ||
                operatorGear[GearType.KIT2] == null) {
                return false
            }
        }

        return true
    }

    suspend fun createTeam(teamViewModel: TeamViewModel): Boolean {
        if (!isFormValid()) return false

        isCreating = true
        try {
            // Prepare operator IDs
            val operatorIds = selectedOperators.mapNotNull { it?.id }

            // Prepare weapon IDs
            val weaponIds = selectedWeapons.mapNotNull { it?.id }

            // Prepare gear IDs - for each operator, get gear IDs in order: [armor, gloves, kit1, kit2]
            val gearIds = mutableListOf<List<Int>>()
            for (i in 0..3) {
                val operatorGear = selectedGear[i]!!
                val gearList = listOf(
                    operatorGear[GearType.ARMOR]!!.id,
                    operatorGear[GearType.GLOVES]!!.id,
                    operatorGear[GearType.KIT1]!!.id,
                    operatorGear[GearType.KIT2]!!.id
                )
                gearIds.add(gearList)
            }

            val request = CreateTeamRequest(
                teamName = teamName,
                description = description,
                operatorIds = operatorIds,
                weaponIds = weaponIds,
                gearIds = gearIds
            )

            val success = teamViewModel.createTeam(request)
            return success

        } catch (e: Exception) {
            errorMessage = "Failed to create team: ${e.message}"
            return false
        } finally {
            isCreating = false
        }
    }

    fun clearError() {
        errorMessage = null
    }
}