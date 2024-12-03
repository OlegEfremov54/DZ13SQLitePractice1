package com.example.dz13sqlitepractice1

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomListAdapter (private val context: Context, productList: MutableList<Product>) :
    ArrayAdapter<Product>(context, R.layout.list_item, productList){
    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val product = getItem(position)
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val idText = view?.findViewById<TextView>(R.id.idTV)
        val nameText = view?.findViewById<TextView>(R.id.nameTV)
        val weightText = view?.findViewById<TextView>(R.id.weightTV)
        val priceText = view?.findViewById<TextView>(R.id.priceTV)
        idText?.text = "Код: ${product?.productId}"
        nameText?.text = "Название: ${product?.productName}"
        weightText?.text = "Вес: ${product?.productWeight}"
        priceText?.text = "Цена: ${product?.productPrice}"
        return view!!
    }
}