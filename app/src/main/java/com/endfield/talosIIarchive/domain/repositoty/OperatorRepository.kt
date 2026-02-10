package com.endfield.talosIIarchive.domain.repositoty

import com.endfield.talosIIarchive.domain.models.Operator


interface OperatorRepository {
    suspend fun getAllOperators(): List<Operator>
    suspend fun getOperatorById(id: Int): Operator?
}