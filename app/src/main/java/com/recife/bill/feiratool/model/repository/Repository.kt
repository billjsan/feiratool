package com.recife.bill.feiratool.model.repository

import android.content.Context
import com.recife.bill.feiratool.model.repository.persistence.AirPowerDatabase
import com.recife.bill.feiratool.model.repository.persistence.model.ShopItem
import com.recife.bill.feiratool.model.repository.persistence.model.ShoppItemEntry
import com.recife.bill.feiratool.model.repository.persistence.model.ShoppList
import com.recife.bill.feiratool.model.repository.persistence.model.ShoppListWithEntries
import com.recife.bill.feiratool.model.utils.AirPowerLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class Repository private constructor(context: Context) {
    private val db = AirPowerDatabase.getDataBaseInstance(context)

    private val _allShoppingLists = MutableStateFlow<List<ShoppListWithEntries>>(emptyList())
    val allShoppingLists: StateFlow<List<ShoppListWithEntries>> get() = _allShoppingLists

    private val _currentShoppingList = MutableStateFlow<ShoppListWithEntries>(getEmptyCurrentList())
    val currentShoppingList: StateFlow<ShoppListWithEntries> get() = _currentShoppingList


    companion object {
        @Volatile
        private var instance: Repository? = null
        fun build(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        if (AirPowerLog.ISLOGABLE)
                            AirPowerLog.d(TAG, "build()")
                        instance = Repository(context)
                    }
                }
            }
        }

        fun getInstance(): Repository {
            return instance
                ?: throw IllegalStateException("AirPowerRepository not initialized. Call build() first.")
        }

        private val TAG = Repository::class.simpleName
    }

    suspend fun loadAllShoppingLists() {
        val allLists = db.shoppListDao().getAllShoppListsWithEntries()
        _allShoppingLists.value = allLists
    }

    suspend fun createShoppList(
        shoppList: ShoppList
    ) {
        db.shoppListDao().insertShoppList(shoppList)
        val allLists = db.shoppListDao().getAllShoppListsWithEntries()
        _allShoppingLists.value = allLists
    }

    suspend fun addNewItemAndEntry(
        shoppItem: ShopItem,
        shoppItemEntry: ShoppItemEntry
    ) {
        db.shoppListDao().insertShoppItem(shoppItem)
        db.shoppListDao().insertShoppItemEntry(shoppItemEntry)

        updateListTotalsAddEntry(shoppItemEntry.listId, shoppItemEntry)
    }

    private suspend fun updateListTotalsAddEntry(
        listId: String,
        shoppItemEntry: ShoppItemEntry
    ) {
        val listById = db.shoppListDao().getShoppListWithEntriesByListId(listId)

        if (listById != null) {
            val newTotalValue = listById.shoppList.listValue + shoppItemEntry.itemsValue
            val newItemsCount = listById.shoppList.itemsCount + shoppItemEntry.itemCount

            listById.shoppList.itemsCount = newItemsCount
            listById.shoppList.listValue = newTotalValue

            db.shoppListDao().updateShoppList(listById.shoppList)

            retrieveCurrentShoppList(listId)
            loadAllShoppingLists()
        }
    }

    private suspend fun updateListTotalsRemoveEntry(
        listId: String,
        shoppItemEntry: ShoppItemEntry
    ) {
        val listById = db.shoppListDao().getShoppListWithEntriesByListId(listId)

        if (listById != null) {
            val newTotalValue = listById.shoppList.listValue - shoppItemEntry.itemsValue
            val newItemsCount = listById.shoppList.itemsCount - shoppItemEntry.itemCount

            listById.shoppList.itemsCount = newItemsCount
            listById.shoppList.listValue = newTotalValue

            db.shoppListDao().updateShoppList(listById.shoppList)

            retrieveCurrentShoppList(listId)
            loadAllShoppingLists()
        }
    }

    suspend fun deleteItemEntry(
        shoppItemEntry: ShoppItemEntry
    ) {
        db.shoppListDao().deleteShoppItemEntry(shoppItemEntry)
        updateListTotalsRemoveEntry(shoppItemEntry.listId, shoppItemEntry)
    }

    suspend fun retrieveCurrentShoppList(listId: String) {
        val response = db.shoppListDao().getShoppListWithEntriesByListId(listId)
        if (response != null) {
            _currentShoppingList.value = response
        } else {
            AirPowerLog.e(TAG, "retrieveCurrentShoppList() error for id: $listId")
        }
    }

    private fun getEmptyCurrentList(): ShoppListWithEntries {
        return ShoppListWithEntries(
            shoppList = ShoppList(
                id = UUID.randomUUID().toString(),
                name = "",
                date = "",
                listValue = 0.0,
                budget = 0.0,
                itemsCount = 0
            ),
            entries = emptyList()
        )
    }

    suspend fun deleteShoppList(shoppList: ShoppList) {
        db.shoppListDao().deleteShoppList(shoppList)
        loadAllShoppingLists()
    }

    suspend fun getLatestList(): ShoppListWithEntries? {
        return db.shoppListDao().getLatestListWithEntries()
    }
}