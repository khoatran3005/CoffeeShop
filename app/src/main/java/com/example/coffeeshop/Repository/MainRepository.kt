package com.example.coffeeshop.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.coffeeshop.Domain.BannerModel
import com.example.coffeeshop.Domain.CategoryModel
import com.example.coffeeshop.Domain.ItemsModel
import com.google.firebase.database.Query

class MainRepository {
    private val firebaseDatatbase = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<BannerModel>>{
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatatbase.getReference("Banner")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>>{
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatatbase.getReference("Category")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>>{
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatatbase.getReference("Popular")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return listData
    }

    fun loadItemCategory(categoryId:String): LiveData<MutableList<ItemsModel>>{
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref=firebaseDatatbase.getReference("Items")
        val query: Query =ref.orderByChild("categoryId").equalTo(categoryId)

        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                itemsLiveData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return itemsLiveData
    }

    fun loadFavoriteItems(userId: String): LiveData<MutableList<ItemsModel>> {
        val favoriteItemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val favoritesRef = firebaseDatatbase.getReference("Users").child(userId).child("Favorites")

        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteIds = snapshot.children.mapNotNull { it.key }
                val items = mutableListOf<ItemsModel>()
                var loadedCount = 0

                if (favoriteIds.isNotEmpty()) {
                    val itemsRef = firebaseDatatbase.getReference("Items")
                    for (id in favoriteIds) {
                        itemsRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(itemSnapshot: DataSnapshot) {
                                val item = itemSnapshot.getValue(ItemsModel::class.java)
                                if (item != null) {
                                    items.add(item)
                                }
                                loadedCount++
                                if (loadedCount == favoriteIds.size) {
                                    favoriteItemsLiveData.value = items
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                loadedCount++
                                if (loadedCount == favoriteIds.size) {
                                    favoriteItemsLiveData.value = items
                                }
                            }
                        })
                    }
                } else {
                    favoriteItemsLiveData.value = items // Empty list if no favorites
                }
            }

            override fun onCancelled(error: DatabaseError) {
                favoriteItemsLiveData.value = mutableListOf()
            }
        })

        return favoriteItemsLiveData
    }
}