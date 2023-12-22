package org.niklasunrau.pqcmessenger

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.niklasunrau.pqcmessenger.domain.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isUserSignedIn(): Boolean {
        return authRepository.isUserSignedIn
    }

}