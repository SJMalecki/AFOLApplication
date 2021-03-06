package com.example.mysets.view.model.detailViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mysets.models.LegoSet
import com.example.mysets.models.MOCResult
import com.example.mysets.network.Result
import com.example.mysets.repositories.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DetailViewModel(private val repository: Repository) : ViewModel() {

    private val job = Job()
    private val coroutineContext: CoroutineContext get() = job + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val getMocsSuccess = MutableLiveData<MOCResult>()
    val getMocsError = MutableLiveData<String>()
    val getMocsException = MutableLiveData<Exception>()

    override fun onCleared() {
        super.onCleared()
        cancelJob()
    }

    fun getMOCs(setNumber: String) {
        scope.launch {
            when (val mocsRespond = repository.getLegoAlternatives(setNumber)) {
                is Result.Success -> getMocsSuccess.postValue(mocsRespond.data)
                is Result.Error -> getMocsError.postValue(mocsRespond.error)
                is Result.Exception -> getMocsException.postValue(mocsRespond.exception)
            }
        }
    }

    private fun cancelJob() = job.cancel()
    fun addToMySets(legoSet: LegoSet) = scope.launch {
        repository.addToMySets(legoSet)
    }

    suspend fun getListOfMySets(): LiveData<MutableList<LegoSet>> {
        return withContext(Dispatchers.IO) {
            return@withContext repository.getMySets()
        }
    }

    fun removeFromMySets(legoSet: LegoSet) = scope.launch {
        repository.removeFromMySets(legoSet)
    }
}