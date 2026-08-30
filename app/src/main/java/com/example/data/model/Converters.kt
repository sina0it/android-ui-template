package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

object JsonHelper {

    fun parseColorOptions(json: String?): List<ColorOption> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<ColorOption>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ColorOption(
                        name = obj.optString("name", "مشکی"),
                        hex = obj.optString("hex", "#1E293B"),
                        inStock = obj.optBoolean("inStock", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonColorOptions(list: List<ColorOption>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("name", it.name)
            obj.put("hex", it.hex)
            obj.put("inStock", it.inStock)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonStringList(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    fun parseSpecGroups(json: String?): List<SpecGroup> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<SpecGroup>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val groupName = obj.optString("groupName", "مشخصات کلی")
                val itemsArray = obj.optJSONArray("items") ?: JSONArray()
                val items = mutableListOf<SpecItem>()
                for (j in 0 until itemsArray.length()) {
                    val itemObj = itemsArray.getJSONObject(j)
                    items.add(
                        SpecItem(
                            title = itemObj.optString("title", ""),
                            value = itemObj.optString("value", "")
                        )
                    )
                }
                list.add(SpecGroup(groupName, items))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonSpecGroups(list: List<SpecGroup>): String {
        val array = JSONArray()
        list.forEach { group ->
            val gObj = JSONObject()
            gObj.put("groupName", group.groupName)
            val itemsArray = JSONArray()
            group.items.forEach { item ->
                val iObj = JSONObject()
                iObj.put("title", item.title)
                iObj.put("value", item.value)
                itemsArray.put(iObj)
            }
            gObj.put("items", itemsArray)
            array.put(gObj)
        }
        return array.toString()
    }

    fun parseAddresses(json: String?): List<UserAddress> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<UserAddress>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    UserAddress(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        title = obj.optString("title", "منزل"),
                        fullAddress = obj.optString("fullAddress", ""),
                        city = obj.optString("city", "تهران"),
                        postalCode = obj.optString("postalCode", ""),
                        recipientName = obj.optString("recipientName", ""),
                        recipientPhone = obj.optString("recipientPhone", ""),
                        isDefault = obj.optBoolean("isDefault", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonAddresses(list: List<UserAddress>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("title", it.title)
            obj.put("fullAddress", it.fullAddress)
            obj.put("city", it.city)
            obj.put("postalCode", it.postalCode)
            obj.put("recipientName", it.recipientName)
            obj.put("recipientPhone", it.recipientPhone)
            obj.put("isDefault", it.isDefault)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseOrderItems(json: String?): List<OrderItemDetail> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<OrderItemDetail>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    OrderItemDetail(
                        productId = obj.optLong("productId", 0),
                        productTitle = obj.optString("productTitle", ""),
                        quantity = obj.optInt("quantity", 1),
                        unitPrice = obj.optLong("unitPrice", 0),
                        colorName = obj.optString("colorName", ""),
                        modelName = obj.optString("modelName", ""),
                        imageUrl = obj.optString("imageUrl", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun toJsonOrderItems(list: List<OrderItemDetail>): String {
        val array = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("productId", it.productId)
            obj.put("productTitle", it.productTitle)
            obj.put("quantity", it.quantity)
            obj.put("unitPrice", it.unitPrice)
            obj.put("colorName", it.colorName)
            obj.put("modelName", it.modelName)
            obj.put("imageUrl", it.imageUrl)
            array.put(obj)
        }
        return array.toString()
    }
}
